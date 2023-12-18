package com.example;

import com.example.chat.WebSocketChatServer;
import com.example.chat.Message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import javax.websocket.Session;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(App.class)

public class App {

	@Autowired
	private MockMvc mvc;
	private WebSocketChatServer controller;
	private Session session;
	private static Message.Action actionType;

	@Test
	// test that the application boots up at localhost:port
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

	// test that login view is displayed on endpoint /
	public void testLogin() throws Exception {
		this.mvc.perform(get("/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(content().string(containsString("Chat Room Lounge")));
	}

	// test that chat view is displayed on endpoint /index
	public void testChat() throws Exception {
		this.mvc.perform(get("/index"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("chat"))
				.andExpect(content().string(containsString("Welcome")));
	}

	// testing ENTER action
	public void enterChatTest() throws Exception {
		controller.onOpen(this.session, "");

		this.mvc.perform(post("/index"))
				.andExpect(status().isOk())
				.andExpect(status().isCreated())
				.andExpect(view().name("chat"));

	}

	// testing LEAVE action
	public void leaveChatTest() throws Exception {

		controller.onOpen(this.session, "");
		controller.onClose(this.session, "");
		this.mvc.perform(post("/"))
				.andExpect(status().isOk())
				.andExpect(status().isCreated())
				.andExpect(view().name("login"));

	}
}
