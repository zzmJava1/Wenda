package com.newcode.Dao;

import com.newcode.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {

    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id , content , created_date, has_read ,conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,
            "where conversation_id=#{conversationId}  order by created_date DESC limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    //select *,count(id) as cishu from (select * from message order by created_date desc) t group by conversation_id order by created_date desc;
    //因为MyBatis只能将查出来的数据映射成对象，而message对象中并没有count字段，所以
    //我们使用message的id作为存储的地方
    @Select({"select",INSERT_FIELDS,", count(id) as id from (select * from",TABLE_NAME,
            "where from_id=#{userId} or to_id=#{userId} order by created_date desc)" +
            " t group by conversation_id  order by created_date DESC limit #{offset},#{limit}"})
    /*
    @Select({"select ", INSERT_FIELDS, " , count(id) as id from ( select * from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id order by created_date desc limit #{offset}, #{limit}"})
     */
    List<Message> getConversationList(@Param("userId") int userId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select count(id) from",TABLE_NAME,"where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId")int userId,
                                   @Param("conversationId")String conversationId);

    @Update({"update",TABLE_NAME,"set has_read=1 where from_id=#{userId}"})
    int updateHasRead(@Param("userId")int userId);
}
