package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.FileMessage;
import com.androidex.capbox.base.adapter.HelperAdapter;
import com.androidex.capbox.base.adapter.HelperViewHolder;
import com.androidex.capbox.module.ChatInfoModel;
import com.androidex.capbox.ui.view.AutoLinkTextView;
import com.makeramen.roundedimageview.RoundedImageView;

import static com.androidex.capbox.utils.Constants.VISE_COMMAND_TYPE_FILE;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/4/27
 */
public class ChatAdapter extends HelperAdapter<ChatInfoModel> {
    public ChatAdapter(Context context) {
        super(context, R.layout.item_chat_info_left, R.layout.item_chat_info_right);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, ChatInfoModel chatInfo) {
        if (chatInfo == null) {
            return;
        }
        TextView timeTv;
        AutoLinkTextView msgTv;
        RoundedImageView iv_FriendHead;
        if (chatInfo.isSend()) {
            timeTv = viewHolder.getView(R.id.item_chat_right_time);
            msgTv = viewHolder.getView(R.id.item_chat_right_msg);
            timeTv.setText(chatInfo.getSendTime());
        } else {
            iv_FriendHead = viewHolder.getView(R.id.item_chat_left_icon);
            timeTv = viewHolder.getView(R.id.item_chat_left_time);
            msgTv = viewHolder.getView(R.id.item_chat_left_msg);
            timeTv.setText(chatInfo.getReceiveTime());
            iv_FriendHead.setImageResource(R.mipmap.ic_box_white);
        }
        if (chatInfo.getMessage() != null) {
            if (chatInfo.getMessage().getMsgType() == VISE_COMMAND_TYPE_FILE) {
                if (chatInfo.isSend()) {
                    msgTv.setText("发送文件：" + ((FileMessage) chatInfo.getMessage()).getFileName());
                } else {
                    msgTv.setText("接收文件：" + ((FileMessage) chatInfo.getMessage()).getFileName());
                }
            } else {
                msgTv.setText(chatInfo.getMessage().getMsgContent());
            }
        }
    }

    @Override
    public int checkLayout(int position, ChatInfoModel item) {
        if (item != null && item.isSend()) {
            return 1;
        }
        return 0;
    }
}

