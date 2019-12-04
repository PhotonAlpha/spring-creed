package com.ethan.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {
  private static final String AUDIENCE_UNKNOWN = "unknown";
  private static final String AUDIENCE_WEB = "web";
  private static final String AUDIENCE_MOBILE = "mobile";
  private static final String AUDIENCE_TABLET = "tablet";
  private static final Key keys = Keys.secretKeyFor(SignatureAlgorithm.HS512);

  @Value("${jwt.expiration:86400}")
  private Long expiration;
  @Value("${jwt.issuer:creed}")
  private String issuer;
  @Value("${jwt.issuer:10}")
  private Long leeway;

  public <T>T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(keys)
        .parseClaimsJws(token)
        .getBody();
  }
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }
  public Date getIssuedAtDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getIssuedAt);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public String getAudienceFromToken(String token) {
    return getClaimFromToken(token, Claims::getAudience);
  }

  public Boolean isTokenExpired(String token) {
    final Date expirationDate = getExpirationDateFromToken(token);
    return expirationDate.before(now());
  }

  public Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
    return (lastPasswordReset != null && created.before(lastPasswordReset));
  }
  private String doGenerateToken(Map<String, Object> claims, String subject, String audience) {
    final Date createdDate = now();
    final Date expirationDate = calculateExpirationDate(createdDate);
    return Jwts.builder()
        .setIssuer(issuer)
        .setClaims(claims)
        .setSubject(subject)
        .setAudience(audience)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(keys)
        .compact();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    // final Date createDate = getIssuedAtDateFromToken(token);
    return (username.equals(userDetails.getUsername())
        && !isTokenExpired(token));
    // && !isCreatedBeforeLastPasswordReset(createDate, ((JwtUser) userDetails).getLastPasswordResetDate()));
  }

  private Date calculateExpirationDate(Date createdDate) {
    final LocalDateTime localTime = createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    LocalDateTime expirationTime = localTime.plusSeconds(expiration);
    return Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public String generateToken(UserDetails userDetails, Device device) {
    Map<String, Object> claims = new HashMap<>();
    final List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    claims.put("role", roles);
    return doGenerateToken(claims, userDetails.getUsername(), generateAudience(device));
  }
  public String refreshToken(String token) {
    final Date createdDate = now();
    final Date expirationDate = calculateExpirationDate(createdDate);
    final Claims claims = getAllClaimsFromToken(token);
    claims.setIssuedAt(createdDate);
    claims.setExpiration(expirationDate);
    return Jwts.builder()
        .setClaims(claims)
        .signWith(keys)
        .compact();
  }

  private String generateAudience(Device device) {
    String audience = AUDIENCE_UNKNOWN;
    if (device.isNormal()) {
      audience = AUDIENCE_WEB;
    } else if (device.isTablet()) {
      audience = AUDIENCE_TABLET;
    } else if (device.isMobile()) {
      audience = AUDIENCE_MOBILE;
    }
    return audience;
  }

  private Date now() {
    return new Date();
  }

}
