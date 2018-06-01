package com.bclould.tocotalk.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;


import com.bclould.tocotalk.history.DBManager;
import com.bclould.tocotalk.ui.activity.HTMLActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;

/**
 * Created by xux on 2016/7/6.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class HyperLinkUtil {
    private OnChangeLinkListener mOnChangeLinkListener;

    public interface OnChangeLinkListener{
        void changeLink(String message,String oldMessage);
    }


    public  SpannableStringBuilder getHyperClickableSpan(Context context, SpannableStringBuilder spanStr) {
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

    public  SpannableStringBuilder getHyperClickableSpan(Context context, SpannableStringBuilder spanStr, boolean isChatLeft, int messageId, DBManager dbManager, OnChangeLinkListener onChangeLinkListener) {
        mOnChangeLinkListener=onChangeLinkListener;
        if (spanStr == null || spanStr.length() == 0) {
            return spanStr;
        }
//        Matcher m = Patterns.WEB_URL.matcher(spanStr);
        Matcher m = UtilTool.searchUrl().matcher(spanStr);
        while (m.find()) {
            spanStr = showHyperLinkString(context, spanStr, m.group(), m.start(),isChatLeft);
            String html5Url=m.group();
            if (!UtilTool.checkLinkedExe(html5Url)) {
                html5Url = "http://" + html5Url;
            }
            spanStr=getTitleText(html5Url,m.start(),spanStr,messageId,dbManager,isChatLeft);
        }
        return spanStr;
    }

    private  SpannableStringBuilder getTitleText(final String html5Url, int start, final SpannableStringBuilder spanStr, final int messageId, final DBManager dbManager, boolean isChatLeft) {
        String oldString=spanStr.toString().substring(0,start);
        int lastRight=oldString.lastIndexOf("\n");
        if(lastRight>0&&lastRight==start-1){
            oldString=oldString.substring(0,lastRight);
            int lastLeft=oldString.lastIndexOf("\n");
            if(lastLeft>=0){
                // TODO: 2018/5/31 改變字體顏色
                int color;
                if(isChatLeft){
                    color=0xff42B0FF;
                }else{
                    color=0xffffffff;
                }
                if(lastLeft==0){
                    spanStr.replace(0,1,"");
                    start=start-2;
                }
                StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);
                spanStr.setSpan(new ForegroundColorSpan(color), lastLeft, start, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);//背景色
                spanStr.setSpan(span, lastLeft, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spanStr;
            }
        }
        final int finalStart = start;
        new Thread(){
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(html5Url).get();
                    if(!StringUtils.isEmpty(doc.title())) {
                        String oldString=spanStr.toString();
                        spanStr.insert(finalStart, "\n" + doc.title() + "\n");
                        dbManager.updateMessage(messageId,spanStr.toString());
                        mOnChangeLinkListener.changeLink(spanStr.toString(),oldString);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
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
