package com.bclould.tocotalk.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;


import com.bclould.tocotalk.ui.activity.HTMLActivity;

import java.util.regex.Matcher;

/**
 * Created by xux on 2016/7/6.
 */
public class HyperLinkUtil {
    public static SpannableStringBuilder getHyperClickableSpan(Context context, SpannableStringBuilder spanStr) {
        if (spanStr == null || spanStr.length() == 0) {
            return spanStr;
        }
        Matcher m = Patterns.WEB_URL.matcher(spanStr);
//        Matcher m = Tools.searchUrl().matcher(spanStr);
        while (m.find()) {
            spanStr = showHyperLinkString(context, spanStr, m.group(), m.start());
        }
        return spanStr;
    }
    public static SpannableStringBuilder getHyperClickableSpan(Context context, SpannableStringBuilder spanStr, boolean isChatLeft) {
        if (spanStr == null || spanStr.length() == 0) {
            return spanStr;
        }
//        Matcher m = Patterns.WEB_URL.matcher(spanStr);
        Matcher m = UtilTool.searchUrl().matcher(spanStr);
        while (m.find()) {
            spanStr = showHyperLinkString(context, spanStr, m.group(), m.start(),isChatLeft);
        }
        return spanStr;
    }

    private static SpannableStringBuilder showHyperLinkString(final Context context, SpannableStringBuilder spanStr, final String url, int start) {
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xff172a88);
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HTMLActivity.class);
                intent.putExtra("html5Url", url);
                context.startActivity(intent);
            }
        }, start, start + url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sendText.setMovementMethod(LinkMovementMethod.getInstance());
        return spanStr;
    }

    private static SpannableStringBuilder showHyperLinkString(final Context context, SpannableStringBuilder spanStr, final String url, int start, final boolean isChatLeft) {
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                if(isChatLeft){
                    ds.setColor(0xff42B0FF);
                }else{
                    ds.setColor(0xffffffff);
                }

                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HTMLActivity.class);
                intent.putExtra("html5Url", url);
                context.startActivity(intent);
            }
        }, start, start + url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        sendText.setMovementMethod(LinkMovementMethod.getInstance());
        return spanStr;
    }

    public static SpannableStringBuilder setSelfUrlSpan(final Context context, SpannableStringBuilder builder) {
        if (builder == null || builder.length() == 0) {
            return builder;
        }
        URLSpan[] spans = builder.getSpans(0, builder.length(), URLSpan.class);
        if (spans != null && spans.length > 0) {
            int start = 0;
            int end = 0;
            for (final URLSpan span : spans) {
                start = builder.getSpanStart(span);
                end = builder.getSpanEnd(span);
                // to replace each link span with customized ClickableSpan
                builder.removeSpan(span);
                builder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(0xff172a88);
                                        ds.setUnderlineText(true);
                                    }

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, HTMLActivity.class);
                                        intent.putExtra("html5Url", span.getURL().toString());
                                        context.startActivity(intent);
                                    }
                                },
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }
}
