package com.bclould.tocotalk.model;

/**
 * Created by GA on 2018/3/4.
 */

public class AwsInfo {

    /**
     * status : 1
     * data : {"AccessKeyId":"ASIAJ35LSPF3FVQ7J6DQ","SecretAccessKey":"Q5SKX5El/9Waeo8zShxYUmYx/oEIC0QHtGqPY/Qs","SessionToken":"FQoDYXdzELj//////////wEaDIKMLMnI21KBk5KK0CKsAcnIZSW0u9kTIg1sPGPs1mtC7LaidxpVU/qOOIN+zM30Nh2dDK5yRyOhuZkJ2mVmt+YR0qMtYfrO50H/2AUDUW8XK3r3CVgHP/R4ulA4d2z+ZTU1h5qXic5t2W3T7XD338UUQgF/uGizcoYuwLQXoTIIE8AcEbovDfHac3L6eFBJ6ZvTeM4utP3mRsclAvFV//6KQToa5Gzz43ZjasHJ2U/iP62xReGxlR1P7Y0opfnT1AU=","Expiration":"2018-02-27T18:37:25Z","region":"ap-northeast-2","bucket":"bclould"}
     */

    private int status;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * AccessKeyId : ASIAJ35LSPF3FVQ7J6DQ
         * SecretAccessKey : Q5SKX5El/9Waeo8zShxYUmYx/oEIC0QHtGqPY/Qs
         * SessionToken : FQoDYXdzELj//////////wEaDIKMLMnI21KBk5KK0CKsAcnIZSW0u9kTIg1sPGPs1mtC7LaidxpVU/qOOIN+zM30Nh2dDK5yRyOhuZkJ2mVmt+YR0qMtYfrO50H/2AUDUW8XK3r3CVgHP/R4ulA4d2z+ZTU1h5qXic5t2W3T7XD338UUQgF/uGizcoYuwLQXoTIIE8AcEbovDfHac3L6eFBJ6ZvTeM4utP3mRsclAvFV//6KQToa5Gzz43ZjasHJ2U/iP62xReGxlR1P7Y0opfnT1AU=
         * Expiration : 2018-02-27T18:37:25Z
         * region : ap-northeast-2
         * bucket : bclould
         */

        private String AccessKeyId;
        private String SecretAccessKey;
        private String SessionToken;
        private String Expiration;
        private String region;
        private String bucket;

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getSecretAccessKey() {
            return SecretAccessKey;
        }

        public void setSecretAccessKey(String SecretAccessKey) {
            this.SecretAccessKey = SecretAccessKey;
        }

        public String getSessionToken() {
            return SessionToken;
        }

        public void setSessionToken(String SessionToken) {
            this.SessionToken = SessionToken;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }
}
