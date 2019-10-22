package com.newcode.Wenda;

import com.newcode.Dao.QuestionDao;
import com.newcode.Dao.UserDao;
import com.newcode.WendaApplication;
import com.newcode.model.EntityType;
import com.newcode.model.Question;
import com.newcode.model.User;
import com.newcode.service.FollowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDataBaseTests {

	@Autowired
	UserDao userDao;
	@Autowired
	QuestionDao questionDao;

	@Autowired
	FollowService followService;
	@Test
	public void contextLoads() {
		Random random = new Random();
		for (int i = 1; i < 11; ++i) {
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			userDao.addUser(user);

			for (int j = 0; j < i ; j++) {

				//让前面的用户都关注新加入的用户
				followService.follow(j, EntityType.ENTITY_USER,i);

			}
			user.setPassword("xx");
			userDao.update(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
			question.setCreatedDate(date);
			question.setUserId(i + 1);
			question.setTitle(String.format("TITLE{%d}", i));
			question.setContent(String.format("Balaababalalalal Content %d", i));
			questionDao.addQuestion(question);
		}
	}

}
