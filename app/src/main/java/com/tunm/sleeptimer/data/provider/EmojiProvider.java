package com.tunm.sleeptimer.data.provider;

import android.content.Context;

import com.tunm.sleeptimer.R;

import java.util.Random;

public class EmojiProvider {
    private int[] mEmojiArray;
    private Context mContext;

    public EmojiProvider(Context context) {
        this.mContext = context;
        mEmojiArray = context.getResources().getIntArray(R.array.emojis_array);
    }

    public String getEmojis() {
        int emoji_1 = mEmojiArray[new Random().nextInt(mEmojiArray.length)];
        int emoji_2 = mEmojiArray[new Random().nextInt(mEmojiArray.length)];
        return getEmojiByUnicode(emoji_1) + " " + getEmojiByUnicode(emoji_2);
    }

    private String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
