package pers.sharedFileSystem.entity;

/**
 * 存储服务器结点性能评价指标
 */
public class Indicators {
    /**
     * CPU占用率在性能评价指标中所占的权重
     */
    public double WeightCPU;
    /**
     * 内存占用率在性能评价指标中所占的权重
     */
    public double WeightMemory;
    /**
     * 磁盘占用率在性能评价指标中所占的权重
     */
    public double WeightDisk;
}