package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {


        System.out.println("----user test initiates-----");
        UserService userService = new JCFUserService();
        User user = new User("JaneDoe", "Janedoe@gmail.com",9876,"JD1");
        userService.create(user);
        System.out.println("Created: " + user.getUsername());
        userService.read(user.getId(), user);
        System.out.println("Read: " + user.getUsername());
        userService.readAll();
        System.out.println("Read All: " + user.getUsername());
        User updatedUser = new User("JaneDoe Updated", "Janedoe123@gmail.com",9876,"JD1");
        userService.update(user.getId(), updatedUser);
        System.out.println("Updated user");
        userService.read(user.getId(), user);
        System.out.println("After update: " + updatedUser.getUsername());
        userService.delete(user.getId());
        System.out.println("Deleted user");
        User deletedUser = userService.read(user.getId(), user);
        System.out.println("After delete: " + (deletedUser == null ? "confirmed deleted" : "Still exists"));
        System.out.println("----user test ends-----");

        System.out.println("----message test initiates-----");
        MessageService messageService = new JCFMessageService();
        Message message = new Message("Inquiry", "JaneDoe","Admin");
        messageService.create(message);
        System.out.println("Created: " + message.getContent());
        messageService.read(message.getId(), message);
        System.out.println("Read: " + message.getContent());
        messageService.readAll();
        System.out.println("Read All: " + message.getContent());
        Message updatedMessage = new Message("General Inquiry", "JaneDoe Updated","Admin");
        messageService.update(message.getId(), updatedMessage);
        System.out.println("Updated Message");
        messageService.read(user.getId(),message);
        System.out.println("After update: " + updatedMessage.getContent());
        messageService.delete(message.getContent());
        System.out.println("Deleted Message");
        Message deletedMessage = messageService.read(user.getId(),message);
        System.out.println((("After delete: " + deletedMessage) == null) ? "confirmed deleted" : "Still exists");
        System.out.println("----message test ends-----");

        System.out.println("----channel test initiates-----");
        ChannelService channelService = new JCFChannelService();
        Channel channel = new Channel("QnA","help","help","This is a QnA Channel!", "verifiedMember" );
        channelService.create(channel);
        System.out.println("Created: " + channel.getName());
        channelService.read(channel.getName());
        System.out.println("Read: " + channel.getName());
        List<Channel> allChannels = channelService.readAll();
        System.out.println("Read All: " + allChannels.size() + " channels");
        Channel updatedChannel = new Channel("QnA Updated","help","help","This is a QnA Channel! Feel free to post :)", "verifiedMember" );
        channelService.update(channel.getName(), updatedChannel);
        System.out.println("Updated Channel");
        channelService.read(channel.getName());
        System.out.println("After update: " + updatedChannel.getName());
        channelService.delete(channel.getName());
        System.out.println("Deleted Channel");
        Channel deletedChannel = channelService.read(channel.getName());
        System.out.println("After delete: " + (deletedChannel == null ? "confirmed deleted" : "Still exists"));
        System.out.println("----channel test ends-----");













    }
}
