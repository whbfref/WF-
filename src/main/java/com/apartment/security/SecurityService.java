package com.apartment.security;

/**
 * 安全服务接口，用于自定义权限验证
 */
public interface SecurityService {

    /**
     * 检查用户是否为合同的租户
     * @param contractId 合同ID
     * @param username 用户名
     * @return 是否为租户
     */
    boolean isContractTenant(Long contractId, String username);

    /**
     * 检查用户是否为物业的房东
     * @param propertyId 物业ID
     * @param username 用户名
     * @return 是否为房东
     */
    boolean isPropertyLandlord(Long propertyId, String username);

    /**
     * 检查用户是否为房间的租户
     * @param roomId 房间ID
     * @param username 用户名
     * @return 是否为租户
     */
    boolean isRoomTenant(Long roomId, String username);
}