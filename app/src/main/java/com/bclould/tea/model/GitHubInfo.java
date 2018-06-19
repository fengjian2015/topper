package com.bclould.tea.model;

import java.util.List;

/**
 * Created by GA on 2018/3/14.
 */

public class GitHubInfo {


    /**
     * id : 17738
     * tag_name : v0.49
     * target_commitish : master
     * prerelease : false
     * name : 234
     * body : 234234234234
     * author : {"id":747477,"login":"iwtxi","name":"BoBoXi","avatar_url":"https://gitee.com/assets/no_portrait.png","url":"https://gitee.com/api/v5/users/iwtxi","html_url":"https://gitee.com/iwtxi","followers_url":"https://gitee.com/api/v5/users/iwtxi/followers","following_url":"https://gitee.com/api/v5/users/iwtxi/following_url{/other_user}","gists_url":"https://gitee.com/api/v5/users/iwtxi/gists{/gist_id}","starred_url":"https://gitee.com/api/v5/users/iwtxi/starred{/owner}{/repo}","subscriptions_url":"https://gitee.com/api/v5/users/iwtxi/subscriptions","organizations_url":"https://gitee.com/api/v5/users/iwtxi/orgs","repos_url":"https://gitee.com/api/v5/users/iwtxi/repos","events_url":"https://gitee.com/api/v5/users/iwtxi/events{/privacy}","received_events_url":"https://gitee.com/api/v5/users/iwtxi/received_events","type":"User","site_admin":false}
     * created_at : 2018-06-16T14:21:53+08:00
     * assets : [{"browser_download_url":"https://gitee.com/iwtxi/234/attach_files/download?i=143722&u=http%3A%2F%2Ffiles.git.oschina.net%2Fgroup1%2FM00%2F03%2FF6%2FPaAvDFskq7WAYNspAYQaaFCYt44074.apk%3Ftoken%3D075b7450f46930ff44b6266f22c87723%26ts%3D1529377929%26attname%3Dtopperchat_release_v0.49.apk"},{"browser_download_url":"https://gitee.com/iwtxi/234/repository/archive/v0.49.zip"}]
     */

    private int id;
    private String tag_name;
    private String target_commitish;
    private boolean prerelease;
    private String name;
    private String body;
    private AuthorBean author;
    private String created_at;
    private List<AssetsBean> assets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTarget_commitish() {
        return target_commitish;
    }

    public void setTarget_commitish(String target_commitish) {
        this.target_commitish = target_commitish;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public AuthorBean getAuthor() {
        return author;
    }

    public void setAuthor(AuthorBean author) {
        this.author = author;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<AssetsBean> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetsBean> assets) {
        this.assets = assets;
    }

    public static class AuthorBean {
        /**
         * id : 747477
         * login : iwtxi
         * name : BoBoXi
         * avatar_url : https://gitee.com/assets/no_portrait.png
         * url : https://gitee.com/api/v5/users/iwtxi
         * html_url : https://gitee.com/iwtxi
         * followers_url : https://gitee.com/api/v5/users/iwtxi/followers
         * following_url : https://gitee.com/api/v5/users/iwtxi/following_url{/other_user}
         * gists_url : https://gitee.com/api/v5/users/iwtxi/gists{/gist_id}
         * starred_url : https://gitee.com/api/v5/users/iwtxi/starred{/owner}{/repo}
         * subscriptions_url : https://gitee.com/api/v5/users/iwtxi/subscriptions
         * organizations_url : https://gitee.com/api/v5/users/iwtxi/orgs
         * repos_url : https://gitee.com/api/v5/users/iwtxi/repos
         * events_url : https://gitee.com/api/v5/users/iwtxi/events{/privacy}
         * received_events_url : https://gitee.com/api/v5/users/iwtxi/received_events
         * type : User
         * site_admin : false
         */

        private int id;
        private String login;
        private String name;
        private String avatar_url;
        private String url;
        private String html_url;
        private String followers_url;
        private String following_url;
        private String gists_url;
        private String starred_url;
        private String subscriptions_url;
        private String organizations_url;
        private String repos_url;
        private String events_url;
        private String received_events_url;
        private String type;
        private boolean site_admin;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar_url() {
            return avatar_url;
        }

        public void setAvatar_url(String avatar_url) {
            this.avatar_url = avatar_url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHtml_url() {
            return html_url;
        }

        public void setHtml_url(String html_url) {
            this.html_url = html_url;
        }

        public String getFollowers_url() {
            return followers_url;
        }

        public void setFollowers_url(String followers_url) {
            this.followers_url = followers_url;
        }

        public String getFollowing_url() {
            return following_url;
        }

        public void setFollowing_url(String following_url) {
            this.following_url = following_url;
        }

        public String getGists_url() {
            return gists_url;
        }

        public void setGists_url(String gists_url) {
            this.gists_url = gists_url;
        }

        public String getStarred_url() {
            return starred_url;
        }

        public void setStarred_url(String starred_url) {
            this.starred_url = starred_url;
        }

        public String getSubscriptions_url() {
            return subscriptions_url;
        }

        public void setSubscriptions_url(String subscriptions_url) {
            this.subscriptions_url = subscriptions_url;
        }

        public String getOrganizations_url() {
            return organizations_url;
        }

        public void setOrganizations_url(String organizations_url) {
            this.organizations_url = organizations_url;
        }

        public String getRepos_url() {
            return repos_url;
        }

        public void setRepos_url(String repos_url) {
            this.repos_url = repos_url;
        }

        public String getEvents_url() {
            return events_url;
        }

        public void setEvents_url(String events_url) {
            this.events_url = events_url;
        }

        public String getReceived_events_url() {
            return received_events_url;
        }

        public void setReceived_events_url(String received_events_url) {
            this.received_events_url = received_events_url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isSite_admin() {
            return site_admin;
        }

        public void setSite_admin(boolean site_admin) {
            this.site_admin = site_admin;
        }
    }

    public static class AssetsBean {
        /**
         * browser_download_url : https://gitee.com/iwtxi/234/attach_files/download?i=143722&u=http%3A%2F%2Ffiles.git.oschina.net%2Fgroup1%2FM00%2F03%2FF6%2FPaAvDFskq7WAYNspAYQaaFCYt44074.apk%3Ftoken%3D075b7450f46930ff44b6266f22c87723%26ts%3D1529377929%26attname%3Dtopperchat_release_v0.49.apk
         */

        private String browser_download_url;

        public String getBrowser_download_url() {
            return browser_download_url;
        }

        public void setBrowser_download_url(String browser_download_url) {
            this.browser_download_url = browser_download_url;
        }
    }
}
