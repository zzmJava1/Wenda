package com.newcode.Dao;

import com.newcode.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginDao {
    String table_name = " login_ticket ";
    String INSERT_FIELDS = " user_id,expired,status,ticket ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", table_name, "(", INSERT_FIELDS,
            ") values (#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket ticket);

    //利用ticket查询
    @Select({"select",SELECT_FIELDS,"from",table_name,"where ticket=#{ticket}"})
    LoginTicket selectByTicket(@Param("ticket") String ticket);

    @Update({"update ",table_name,"set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket")String ticket,
                      @Param("status") String status);

}
