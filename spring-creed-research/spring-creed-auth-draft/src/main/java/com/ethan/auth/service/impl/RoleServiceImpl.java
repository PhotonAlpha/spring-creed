package auth.service.impl;

import com.ethan.auth.model.Role;
import com.ethan.auth.repository.RoleRepository;
import com.ethan.auth.service.RoleService;
import com.ethan.auth.vo.ResponseVO;
import com.ethan.auth.vo.RoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ResponseVO findAllRoleVO() {
        List<Role> rolePOList = roleRepository.findAll();
        List<RoleVO> roleVOList = new ArrayList<>();
        rolePOList.forEach(rolePO->{
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(rolePO,roleVO);
            roleVOList.add(roleVO);
        });
        return ResponseVO.success(roleVOList);
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id).get();
    }
}
