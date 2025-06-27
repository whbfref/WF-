package com.apartment.security;

import com.apartment.model.Contract;
import com.apartment.model.Property;
import com.apartment.model.User;
import com.apartment.repository.ContractRepository;
import com.apartment.repository.PropertyRepository;
import com.apartment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 安全服务实现类
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isContractTenant(Long contractId, String username) {
        // 获取用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // 获取合同
        Optional<Contract> contractOpt = contractRepository.findById(contractId);
        if (!contractOpt.isPresent()) {
            return false;
        }
        
        Contract contract = contractOpt.get();
        
        // 检查用户是否为合同的租户
        return contract.getTenantId().equals(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPropertyLandlord(Long propertyId, String username) {
        // 获取用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // 只有LANDLORD角色才能是房东
        if (!"LANDLORD".equals(user.getRole())) {
            return false;
        }
        
        // 获取物业
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (!propertyOpt.isPresent()) {
            return false;
        }
        
        Property property = propertyOpt.get();
        
        // 检查用户是否为物业的房东
        return property.getLandlordId().equals(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoomTenant(Long roomId, String username) {
        // 获取用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // 获取用户的有效合同（暂时不支持按状态查询）
        // List<Contract> activeContracts = contractRepository.findActiveContracts(LocalDate.now());
        List<Contract> contracts = contractRepository.findByTenantId(user.getId());
        
        // 检查是否有关联该房间的合同
        for (Contract contract : contracts) {
            if (contract.getRoomId().equals(roomId)) {
                return true;
            }
        }
        
        return false;
    }
} 