package com.xunda.mo.main.group.activity;

//public class test {
//
//    if ([userModel.hxUserName isEqualToString:messageModel.message.from]) {
//        if ([dict[@"messageType"] isEqualToString:@"apply"]) {
//            cell.str = [NSString stringWithFormat:@" 您已同意添加了%@，现在可以开始聊天了 ",dict[@"toName"]];
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"doubleRecall"]) {
//            cell.str = @" 您撤回了所有消息 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"screenShots"]) {
//            cell.str = @" 对方进行了截屏 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"createGroup"]) {
//
//            cell.dict=dict;
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"addUser"])
//        {
//            cell.addDict=dict;
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"deletUser"]) {
//
//            cell.deletdict=dict;
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"muteOn"]) {
//
//            cell.str=@" 您开启了群员禁言 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"muteOff"]) {
//
//            cell.str=@" 您关闭了群员禁言 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"pushOn"]) {
//
//            cell.str=@" 您开启了成员加群/退群通知 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"pushOff"]) {
//
//            cell.str=@" 您关闭了成员加群/退群通知 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"anonymousOn"]) {
//
//            cell.str=@" 您开启了匿名聊天 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"updateMaster"]) {
//
//                [cell setMaStr:[NSString stringWithFormat:@" 您将群主转让给‘%@’ ",dict[@"userName"]] andLength:[dict[@"userName"] length]];
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"anonymousOff"]) {
//
//            cell.str=@" 您关闭了匿名聊天 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"protectOn"]) {
//
//            cell.str=@" 您开启了群成员保护 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"protectOff"]) {
//
//            cell.str=@" 您关闭了群成员保护 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"groupDestroy"]) {
//
//            cell.str=@" 您解散了群组 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"groupLeave"]) {
//
//            cell.str=@" 您离开了群组 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"addAdmin"])
//            {
//                [cell setAtStr:[NSString stringWithFormat:@" ‘%@’被添加为管理员 ",dict[@"userName"]] andLength:[dict[@"userName"] length]];
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"deletAdmin"])
//        {
//                [cell setAtStr:[NSString stringWithFormat:@" ‘%@’被取消了管理员 ",dict[@"userName"]] andLength:[dict[@"userName"] length]];
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"GroupDoubleRecall"]) {
//            cell.str = @" 您撤回了所有消息 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"GroupDoubleRecall2"]) {
//            if ([dict[@"adminType"] isEqualToString:@"群主"]) {
//                cell.str=@" 群主撤回了所有消息 ";
//            }
//                else
//            {
//                    [cell setAtStr:[NSString stringWithFormat:@" '%@'撤回了所有消息 ",dict[@"sendName"]] andLength:[dict[@"sendName"] length]];
//            }
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"updateGroupDes"])
//        {
//            cell.str = @" 您修改了群简介 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"updateGroupName"])
//        {
//            cell.str = @" 您修改了群聊名称 ";
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"userMuteOn"]||[dict[@"messageType"] isEqualToString:@"userMuteOff"])
//        {
//            EMTextMessageBody *body=(EMTextMessageBody*)messageModel.message.body;
//            cell.str = [NSString stringWithFormat:@" %@ ",body.text];
//        }
//    }
//        else
//    {
//        if ([dict[@"messageType"] isEqualToString:@"apply"]) {
//        cell.str = [NSString stringWithFormat:@" %@已同意添加好友，现在可以开始聊天了 ",dict[@"sendName"]];
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"doubleRecall"]) {
//        cell.str = @" 对方撤回了所有消息 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"createGroup"]) {
//
//        cell.dict=dict;
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"addUser"]) {
//
//        cell.addDict=dict;
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"deletUser"]) {
//
//        cell.deletdict=dict;
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"muteOn"]) {
//
//        cell.str=@" 群主开启了群员禁言 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"updateMaster"]) {
//
//        if ([userModel.userId isEqualToString:dict[@"userId"]]) {
//            cell.str=@" 群主将群转让给您 ";
//        }
//                else
//        {
//                    [cell setMaStr:[NSString stringWithFormat:@" 群主将群转让给‘%@’ ",dict[@"userName"]] andLength:[dict[@"userName"] length]];
//        }
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"muteOff"]) {
//
//        cell.str=@" 群主关闭了群员禁言 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"anonymousOn"]) {
//
//        cell.str=@" 群主开启了匿名聊天 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"anonymousOff"]) {
//
//        cell.str=@" 群主关闭了匿名聊天 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"protectOn"]) {
//
//        cell.str=@" 群主开启了群成员保护 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"pushOn"]) {
//
//        cell.str=[NSString stringWithFormat:@" %@开启了成员加群/退群通知 ",dict[@"userType"]];
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"pushOff"]) {
//
//        cell.str=[NSString stringWithFormat:@" %@关闭了成员加群/退群通知 ",dict[@"userType"]];
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"protectOff"]) {
//
//        cell.str=@" 群主关闭了群成员保护 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"groupDestroy"]) {
//
//        cell.str=@" 群主解散了群组 ";
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"groupLeave"]) {
//
//                [cell setAtStr:[NSString stringWithFormat:@" ‘%@’离开了群组 ",dict[@"sendName"]] andLength:[dict[@"sendName"] length]];
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"addAdmin"])
//        {
//                [cell setAtStr:[NSString stringWithFormat:@" ‘%@’被添加为管理员 ",dict[@"userName"]] andLength:[dict[@"userName"] length]];
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"deletAdmin"])
//        {
//                [cell setAtStr:[NSString stringWithFormat:@" ‘%@’被取消了管理员 ",dict[@"userName"]] andLength:[dict[@"userName"] length]];
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"GroupDoubleRecall"]) {
//        if ([dict[@"adminType"] isEqualToString:@"群主"]) {
//            cell.str=@" 群主撤回了所有消息 ";
//        }
//                else
//        {
//                    [cell setAtStr:[NSString stringWithFormat:@" '%@'撤回了所有消息 ",dict[@"sendName"]] andLength:[dict[@"sendName"] length]];
//        }
//    }
//            else if ([dict[@"messageType"] isEqualToString:@"updateGroupDes"])
//        {
//            if ([dict[@"adminType"] isEqualToString:@"群主"]) {
//            cell.str=@"群主修改了群简介";
//        }
//                else
//            {
//                    [cell setAtStr:[NSString stringWithFormat:@" '%@'修改了群简介 ",dict[@"sendName"]] andLength:[dict[@"sendName"] length]];
//            }
//        }
//            else if ([dict[@"messageType"] isEqualToString:@"updateGroupName"])
//        {
//            if ([dict[@"adminType"] isEqualToString:@"群主"]) {
//            cell.str=@"群主修改了群聊名称";
//        }
//                else
//            {
//                    [cell setAtStr:[NSString stringWithFormat:@" '%@'修改了群聊名称 ",dict[@"sendName"]] andLength:[dict[@"sendName"] length]];
//            }
//        }
//}
