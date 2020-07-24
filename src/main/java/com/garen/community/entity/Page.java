package com.garen.community.entity;

/**
 * @Title : 封装分页相关的信息
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/24 12:22 下午
 */
public class Page {
    //current page
    private int current = 1;
    //show limit
    private int limit = 10;
    //data amount(for calculate page amount)
    private int rows;
    //search path(for re-use page-divide link 复用分页链接(简化))
    private String path;
    
    public int getCurrent() {
        return current;
    }
    
    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }
    
    public int getRows() {
        return rows;
    }
    
    public void setRows(int rows) {
        this.rows = rows;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * @description Get the start line by calculate
     * @param
     * @return int
     */
    public int getOffset() {
        return (current - 1) * limit;
    }
    
    /**
     * @description Count total pages
     * @param
     * @return int
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }
    /**
     * @description Get start page
     * @param
     * @return int
     */
    public int getFrom(){
        int from = current - 2;
        return Math.max(from, 1);
    }
    
    /**
     * @description Get end page
     * @param
     * @return int
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
