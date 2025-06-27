package com.apartment.service.impl;

import com.apartment.repository.UserRepository;
import com.apartment.repository.PropertyRepository;
import com.apartment.repository.ContractRepository;
import com.apartment.repository.BillRepository;
import com.apartment.repository.UtilityBillRepository;
import com.apartment.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UtilityBillRepository utilityBillRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // 获取新增用户数（最近30天）
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            long newUsers = userRepository.countByCreateTimeAfter(thirtyDaysAgo.atStartOfDay());
            
            // 获取物业总数
            long propertyCount = propertyRepository.count();
            
            // 获取所有时间的总收入（从账单表中获取所有已支付的账单）
            BigDecimal totalRevenueBD = billRepository.sumPaidAmount();
            double totalRevenue = totalRevenueBD != null ? totalRevenueBD.doubleValue() : 0.0;
            
            // 获取在租数量（当前有效合同数量）
            long rentedCount = contractRepository.countActiveContracts();
            
            data.put("newUsers", newUsers);
            data.put("propertyCount", propertyCount);
            data.put("totalRevenue", totalRevenue);
            data.put("rentedCount", rentedCount);
            
            // 添加账单统计信息
            long totalBills = billRepository.count();
            List<Object[]> statusStats = billRepository.countByStatus();
            long paidBills = 0;
            long unpaidBills = 0;
            
            for (Object[] stat : statusStats) {
                String status = (String) stat[0];
                Long count = (Long) stat[1];
                if ("PAID".equals(status)) {
                    paidBills = count;
                } else if ("UNPAID".equals(status)) {
                    unpaidBills = count;
                }
            }
            
            data.put("totalBills", totalBills);
            data.put("paidBills", paidBills);
            data.put("unpaidBills", unpaidBills);
            data.put("currentMonth", "all-time"); // 表示所有时间的数据
            
        } catch (Exception e) {
            logger.error("获取仪表盘数据失败", e);
            // 返回默认数据
            data.put("newUsers", 0L);
            data.put("propertyCount", 0L);
            data.put("totalRevenue", 0.0);
            data.put("rentedCount", 0L);
        }
        
        return data;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getVisitSourceData() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> sourceData = new ArrayList<>();
        
        // 这里应该从访问日志或统计表中获取真实数据
        // 目前使用模拟数据
        sourceData.add(createSourceData("小红书", 335));
        sourceData.add(createSourceData("抖音", 310));
        sourceData.add(createSourceData("58同城", 234));
        sourceData.add(createSourceData("自如网站", 135));
        sourceData.add(createSourceData("搜索引擎", 1548));
        
        data.put("sourceData", sourceData);
        return data;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWeeklyActivityData() {
        Map<String, Object> data = new HashMap<>();
        
        // 最近12个月的月份列表
        List<String> months = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        
        // 模拟数据 - 基础租金和月度增长率
        double baseRent = 3500.0; // 基础月租金
        double[] monthlyGrowthRates = {
            0.02, 0.03, 0.01, 0.00, 0.02, 0.03,
            0.05, 0.02, 0.01, 0.04, 0.03, 0.02
        }; // 每月增长率
        
        // 生成最近12个月的数据
        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthStr = date.format(formatter);
            months.add(monthStr);
            
            // 计算模拟的月租金
            double rentAmount = baseRent;
            for (int j = 11; j >= i; j--) {
                rentAmount *= (1 + monthlyGrowthRates[j]);
            }
            
            // 加一些随机波动
            double randomFactor = 1.0 + (Math.random() * 0.1 - 0.05); // ±5%随机波动
            rentAmount *= randomFactor;
            
            // 四舍五入到整数
            rentAmount = Math.round(rentAmount * 100) / 100.0;
            
            amounts.add(rentAmount);
        }
        
        data.put("months", months);
        data.put("amounts", amounts);
        
        return data;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getYearlyRentData() {
        Map<String, Object> data = new HashMap<>();
        
        // 最近24个月的月份列表（未来12个月 + 过去12个月）
        List<String> months = new ArrayList<>();
        List<Double> actual = new ArrayList<>();
        List<Double> predicted = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        
        // 模拟数据 - 基础租金和季节性因子
        double baseRent = 3500.0; // 基础月租金
        
        // 季节性因子 - 一年四季的租金波动
        double[] seasonalFactors = {
            1.00, 0.98, 0.97, 0.96, // 春季 (1-3月)
            0.99, 1.02, 1.05, 1.08, // 夏季 (4-6月)
            1.10, 1.08, 1.05, 1.02  // 秋冬季 (7-12月)
        };
        
        // 年度增长率
        double annualGrowthRate = 0.08; // 8%的年增长率
        
        // 生成过去12个月的实际数据
        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthStr = date.format(formatter);
            months.add(monthStr);
            
            // 计算实际租金（考虑季节性因素和年度增长）
            int monthIndex = date.getMonthValue() - 1; // 0-11
            double seasonalFactor = seasonalFactors[monthIndex];
            int yearsFromBase = date.getYear() - 2020; // 假设2020年为基准年
            
            double rentAmount = baseRent * Math.pow(1 + annualGrowthRate, yearsFromBase) * seasonalFactor;
            
            // 加一些随机波动使数据更真实
            double randomFactor = 1.0 + (Math.random() * 0.08 - 0.04); // ±4%随机波动
            rentAmount *= randomFactor;
            
            // 四舍五入到整数
            rentAmount = Math.round(rentAmount * 100) / 100.0;
            
            actual.add(rentAmount);
        }
        
        // 生成未来12个月的预测数据
        for (int i = 0; i < 12; i++) {
            LocalDate date = now.plusMonths(i + 1);
            String monthStr = date.format(formatter);
            months.add(monthStr);
            
            // 未来月份没有实际数据
            actual.add(0.0);
            
            // 计算预测租金（考虑季节性因素、年度增长和预测不确定性）
            int monthIndex = date.getMonthValue() - 1; // 0-11
            double seasonalFactor = seasonalFactors[monthIndex];
            int yearsFromBase = date.getYear() - 2020; // 假设2020年为基准年
            
            double predictedRent = baseRent * Math.pow(1 + annualGrowthRate, yearsFromBase) * seasonalFactor;
            
            // 预测的不确定性随时间增加
            double uncertaintyFactor = 1.0 + (Math.random() * 0.02 * (i + 1)) - (0.01 * (i + 1));
            predictedRent *= uncertaintyFactor;
            
            // 四舍五入到整数
            predictedRent = Math.round(predictedRent * 100) / 100.0;
            
            predicted.add(predictedRent);
        }
        
        // 为过去12个月添加略微不同的"预测"数据（模拟过去的预测）
        for (int i = 0; i < 12; i++) {
            double actualRent = actual.get(i);
            double predictionError = 1.0 + (Math.random() * 0.1 - 0.05); // ±5%预测误差
            double pastPrediction = actualRent * predictionError;
            
            // 四舍五入到整数
            pastPrediction = Math.round(pastPrediction * 100) / 100.0;
            
            predicted.set(i, pastPrediction);
        }
        
        data.put("months", months);
        data.put("actual", actual);
        data.put("predicted", predicted);
        
        return data;
    }

    private Map<String, Object> createSourceData(String name, int value) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", value);
        return data;
    }

    /**
     * 获取月度租金变化数据（模拟数据）
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMonthlyRentData() {
        Map<String, Object> data = new HashMap<>();
        
        // 生成最近12个月的月份列表
        List<String> months = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate now = LocalDate.now();
        
        // 模拟数据 - 基础租金和季节性波动
        double baseRent = 3800.0; // 基础月租金
        
        // 每月租金波动因子
        double[] monthlyFactors = {
            0.98, 0.97, 0.96, 0.98, 1.02, 1.05, 
            1.08, 1.10, 1.08, 1.05, 1.02, 1.00
        };
        
        // 生成12个月的数据
        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthStr = date.format(formatter);
            months.add(monthStr);
            
            // 获取月份索引(0-11)
            int monthIndex = date.getMonthValue() - 1;
            
            // 计算该月的租金
            double rentAmount = baseRent * monthlyFactors[monthIndex];
            
            // 添加一些随机波动
            double randomFactor = 1.0 + (Math.random() * 0.06 - 0.03); // ±3%的随机波动
            rentAmount *= randomFactor;
            
            // 四舍五入到整数
            rentAmount = Math.round(rentAmount);
            
            amounts.add(rentAmount);
        }
        
        data.put("months", months);
        data.put("amounts", amounts);
        
        return data;
    }
} 