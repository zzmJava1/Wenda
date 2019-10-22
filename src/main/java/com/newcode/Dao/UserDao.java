package com.newcode.Dao;

import com.newcode.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {

    String table_name = " user ";
    String INSERT_FIELDS = " name,password,salt,head_url ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"insert into ", table_name ," (",INSERT_FIELDS, ") values(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select",SELECT_FIELDS,"from",table_name, "Where id = #{id}"})
    User selectById( int id);
    @Update({"update",table_name,"set password = #{password} where id = #{id}"})
    void update(User user);

    @Select({"select",SELECT_FIELDS,"from",table_name,"where name = #{name}"})
    User selectByName(String name);
}
