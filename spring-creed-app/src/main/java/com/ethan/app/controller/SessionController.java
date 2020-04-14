/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/07
 */
package com.ethan.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 查询或移除 spring session
 */
@RestController
@RequestMapping("/sessions")
public class SessionController {
  @Autowired
  private FindByIndexNameSessionRepository sessionRepository;

  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public Map<String, ? extends Session> list(@RequestParam("username") String username) {
    return sessionRepository.findByPrincipalName(username);
  }
  @RequestMapping(value = "/list/evict", method = RequestMethod.GET)
  public ResponseEntity<Boolean> evict(@RequestParam("username") String username) {
    sessionRepository.findByIndexNameAndIndexValue(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, username)
        .keySet().forEach(session -> sessionRepository.deleteById((String)session));
    return ResponseEntity.ok(true);
  }
}
