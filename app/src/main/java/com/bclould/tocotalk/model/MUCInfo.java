package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/1/9.
 */

public class MUCInfo {
    private String account;
    private String room;
    private String nickname;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (nickname.contains("@")) {
            this.nickname = nickname.substring(0, account.indexOf("@"));
            return;
        }
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "MUCInfo [account=" + account + ", room=" + room + ", nickname="
                + nickname + "]";
    }

}
