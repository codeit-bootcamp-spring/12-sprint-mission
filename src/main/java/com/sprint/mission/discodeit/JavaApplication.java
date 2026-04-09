package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;
import java.util.UUID;


public class JavaApplication {
    static void userCRUDTest(UserService userService) {

        User user = userService.create("JaneDoe", "JaneDoe@gmail.com", "JD1234", "JD10002");
        System.out.println("created user: " + user.getId());
        User foundUser = userService.find(user.getId());
        System.out.println("found user: " + foundUser.getId());
        List<User> foundUsers = userService.findAll();
        System.out.println("all users: " + foundUsers.size());
        User updatedUser = userService.update(user.getId(), null, null, null, "JD15645");
        System.out.println("updated user: " + String.join("/", updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getNickname()));
        userService.delete(user.getId());
        List<User> foundUserAfterDelete = userService.findAll();
        System.out.println("all users after delete: " + foundUserAfterDelete.size());
    }

    static void channelCRUDTest(ChannelService channelService) {

        Channel channel = channelService.create(ChannelCategory.valueOf("General"), "QnA", "QnA channel for all! ");
        System.out.println("created channel: " + channel.getId());
        Channel foundChannel = channelService.read(channel.getId());
        System.out.println("found channel: " + foundChannel.getId());
        List<Channel> foundChannels = channelService.readAll();
        System.out.println("all channels: " + foundChannels.size());
        Channel updatedChannel = channelService.update(channel.getId(), "QnA", "QnA channel only for sprint mission related inquiries!! ");
        System.out.println("updated channel: " + updatedChannel.getId());
        channelService.delete(channel.getId());
        List<Channel> foundChannelAfterDelete = channelService.readAll();
        System.out.println("all channels after delete: " + foundChannelAfterDelete.size());
    }

    static void messageCRUDTest(MessageService messageService) {

        String sender = "JaneDoe";
        String receiver = "Admin";
        Message message = messageService.create( "hello", sender, receiver);
        System.out.println("created message: " + message.getId());
        Message foundMessage = messageService.read(message.getId());
        System.out.println("found message: " + foundMessage.getId());
        List<Message> foundMessages = messageService.readAll();
        System.out.println("all messages: " + foundMessages.size());
        Message updatedMessage = messageService.update(message.getId(), "hello, how are you?");
        System.out.println("updated message: " + updatedMessage.getId());
        messageService.delete(message.getId());
        List<Message> foundMessageAfterDelete = messageService.readAll();
        System.out.println("all messages after delete: " + foundMessageAfterDelete.size());

        }
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(messageService);


}
}









