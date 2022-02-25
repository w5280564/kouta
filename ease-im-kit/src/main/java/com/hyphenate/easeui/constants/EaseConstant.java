/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.constants;

public interface EaseConstant {
    String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";


    String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

    String MESSAGE_ATTR_AT_MSG = "em_at_list";
    String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";

    String HISTORY_MSG_ID = "history_msg_id";

    int CHATTYPE_SINGLE = 1;
    int CHATTYPE_GROUP = 2;
    int CHATTYPE_CHATROOM = 3;

    String EXTRA_CHAT_TYPE = "chatType";
    String EXTRA_CONVERSATION_ID = "conversationId";
    String EXTRA_IS_ROAM = "isRoaming";

    String MESSAGE_TYPE_RECALL = "message_recall";

    String MESSAGE_FORWARD = "message_forward";

    String MESSAGE_CHANGE_RECEIVE = "message_receive";
    String MESSAGE_CHANGE_CMD_RECEIVE = "message_cmd_receive";
    String MESSAGE_CHANGE_RECALL = "message_recall";
    String MESSAGE_CHANGE_CHANGE = "message_change";
    String CONVERSATION_DELETE = "conversation_delete";

    String GROUP_LEAVE = "group_leave";

    String DEFAULT_SYSTEM_MESSAGE_ID = "em_system";
    String DEFAULT_SYSTEM_MESSAGE_TYPE = "em_system_type";

}
