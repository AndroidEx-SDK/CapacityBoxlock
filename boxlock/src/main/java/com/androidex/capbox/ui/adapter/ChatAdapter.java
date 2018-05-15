package com.androidex.capbox.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.androidex.capbox.R;
import com.androidex.capbox.base.FileMessage;
import com.androidex.capbox.base.adapter.HelperAdapter;
import com.androidex.capbox.base.adapter.HelperViewHolder;
import com.androidex.capbox.db.ChatRecord;
import com.androidex.capbox.module.ChatInfoModel;
import com.androidex.capbox.ui.view.AutoLinkTextView;
import com.androidex.capbox.utils.CalendarUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import static com.androidex.capbox.utils.Constants.VISE_COMMAND_TYPE_FILE;
import static com.androidex.capbox.utils.Constants.VISE_COMMAND_TYPE_TEXT;

/**
 * @Description:
 * @Author: Liyp
 * @Email: liyp@androidex.cn
 * @Date: 2018/4/27
 */
public class ChatAdapter extends HelperAdapter<ChatRecord> {
    public ChatAdapter(Context context) {
        super(context, R.layout.item_chat_info_left, R.layout.item_chat_info_right);
    }

    @Override
    public void HelpConvert(HelperViewHolder viewHolder, int position, ChatRecord chatInfo) {
        if (chatInfo == null) {
            return;
        }
        TextView timeTv;
        AutoLinkTextView msgTv;
        RoundedImageView iv_FriendHead;
        if (chatInfo.getIsSend().equals("0")) {
            timeTv = viewHolder.getView(R.id.item_chat_right_time);
            msgTv = viewHolder.getView(R.id.item_chat_right_msg);
        } else {
            iv_FriendHead = viewHolder.getView(R.id.item_chat_left_icon);
            timeTv = viewHolder.getView(R.id.item_chat_left_time);
            msgTv = viewHolder.getView(R.id.item_chat_left_msg);
            iv_FriendHead.setImageResource(R.mipmap.ic_box_white);
        }
        timeTv.setText(CalendarUtil.getDateToString(chatInfo.getTime(), CalendarUtil.DATE_AND_TIME));
        if (chatInfo.getMsgContent() != null) {
            if (chatInfo.getMsgType() == VISE_COMMAND_TYPE_TEXT) {
                msgTv.setText(chatInfo.getMsgContent());
            } else {
            }
        }
    }

    @Override
    public int checkLayout(int position, ChatRecord item) {
        if (item != null && item.getIsSend().equals("0")) {
            return 1;
        }
        return 0;
    }
}

