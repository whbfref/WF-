package com.apartment.repository;

import com.apartment.model.LandlordTodo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 房东待办事项数据访问接口
 */
@Repository
public interface LandlordTodoRepository extends JpaRepository<LandlordTodo, Long> {
    
    /**
     * 根据房东ID分页查询待办事项
     * @param landlordId 房东ID
     * @param pageable 分页参数
     * @return 待办事项分页结果
     */
    Page<LandlordTodo> findByLandlordId(Long landlordId, Pageable pageable);
    
    /**
     * 根据房东ID和类型查询待办事项
     * @param landlordId 房东ID
     * @param type 待办类型
     * @param pageable 分页参数
     * @return 待办事项分页结果
     */
    Page<LandlordTodo> findByLandlordIdAndType(Long landlordId, LandlordTodo.TodoType type, Pageable pageable);
    
    /**
     * 根据房东ID和状态查询待办事项
     * @param landlordId 房东ID
     * @param status 待办状态
     * @param pageable 分页参数
     * @return 待办事项分页结果
     */
    Page<LandlordTodo> findByLandlordIdAndStatus(Long landlordId, LandlordTodo.TodoStatus status, Pageable pageable);
    
    /**
     * 根据房东ID统计各状态的待办事项数量
     * @param landlordId 房东ID
     * @return 状态统计列表
     */
    @Query("SELECT t.status, COUNT(t) FROM LandlordTodo t WHERE t.landlordId = :landlordId GROUP BY t.status")
    List<Object[]> countTodosByLandlordIdAndStatus(@Param("landlordId") Long landlordId);
    
    /**
     * 根据房东ID统计各类型的待办事项数量
     * @param landlordId 房东ID
     * @return 类型统计列表
     */
    @Query("SELECT t.type, COUNT(t) FROM LandlordTodo t WHERE t.landlordId = :landlordId GROUP BY t.type")
    List<Object[]> countTodosByLandlordIdAndType(@Param("landlordId") Long landlordId);
    
    /**
     * 根据房东ID查询待处理的待办事项数量
     * @param landlordId 房东ID
     * @return 待处理数量
     */
    long countByLandlordIdAndStatus(Long landlordId, LandlordTodo.TodoStatus status);
} 