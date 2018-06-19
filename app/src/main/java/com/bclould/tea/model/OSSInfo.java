package com.bclould.tea.model;

/**
 * Created by GA on 2018/3/4.
 */

public class OSSInfo {

    /**
     * status : 1
     * data : {"RequestId":"8EB1216E-C173-4632-9C52-A042A8243801","AssumedRoleUser":{"AssumedRoleId":"368101631399122462:client_name","Arn":"acs:ram::1529031778181808:role/aliyunosstokengeneratorrole/client_name"},"Credentials":{"AccessKeySecret":"Htpff29oahozYi8s6DazvzXUTwvDk7VSbNcn85ryT2Wo","AccessKeyId":"STS.NK55dPuRpfcYwnuucce69zP1G","Expiration":"2018-06-13T03:29:02Z","SecurityToken":"CAISkQJ1q6Ft5B2yfSjIr4iAft7kmI1R0aGydUjEkWM2abkVlZWapTz2IH5EfXFpB+sftfU+nWpS6vgYlqJ4T55IQ1Dza8J148yiFqlwtM6T1fau5Jko1bebewHKeSeZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6F3UkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gf6wJ24r/txdaHuFj89ASnkL5N/N2ufcj9P5A8ZMdFPo3rjLAsRM3oyzVN7hVGzqBygZFf9C3P1tPnWAcBuk3fbbGNqYw+dVUjOvZgAd1NqPntiPt/offPkIf6zRlAO+xPWjjYXpqnxMbUwE41wWmFxtwagAF+YAW8xL9hr+ymSzjMCyp6KX4yfQBRCfNsWBZX1TxnK7RqA3kMgOaOg8JMrbV6w2oKP4LdVhCJOmKuYIe2hJTL9fJUzy3z2HbC4q2vcrf5jvSP/CHhzqM8r5AFfyphR0bg+RIXYJtYPDqdz17j/PwL7OzzaWd0m268ZAeH4Fti7w=="},"endpoint":"oss-cn-shenzhen.aliyuncs.com","bucket":"toco","chat_bucket":"topper-chat"}
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
         * RequestId : 8EB1216E-C173-4632-9C52-A042A8243801
         * AssumedRoleUser : {"AssumedRoleId":"368101631399122462:client_name","Arn":"acs:ram::1529031778181808:role/aliyunosstokengeneratorrole/client_name"}
         * Credentials : {"AccessKeySecret":"Htpff29oahozYi8s6DazvzXUTwvDk7VSbNcn85ryT2Wo","AccessKeyId":"STS.NK55dPuRpfcYwnuucce69zP1G","Expiration":"2018-06-13T03:29:02Z","SecurityToken":"CAISkQJ1q6Ft5B2yfSjIr4iAft7kmI1R0aGydUjEkWM2abkVlZWapTz2IH5EfXFpB+sftfU+nWpS6vgYlqJ4T55IQ1Dza8J148yiFqlwtM6T1fau5Jko1bebewHKeSeZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6F3UkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gf6wJ24r/txdaHuFj89ASnkL5N/N2ufcj9P5A8ZMdFPo3rjLAsRM3oyzVN7hVGzqBygZFf9C3P1tPnWAcBuk3fbbGNqYw+dVUjOvZgAd1NqPntiPt/offPkIf6zRlAO+xPWjjYXpqnxMbUwE41wWmFxtwagAF+YAW8xL9hr+ymSzjMCyp6KX4yfQBRCfNsWBZX1TxnK7RqA3kMgOaOg8JMrbV6w2oKP4LdVhCJOmKuYIe2hJTL9fJUzy3z2HbC4q2vcrf5jvSP/CHhzqM8r5AFfyphR0bg+RIXYJtYPDqdz17j/PwL7OzzaWd0m268ZAeH4Fti7w=="}
         * endpoint : oss-cn-shenzhen.aliyuncs.com
         * bucket : toco
         * chat_bucket : topper-chat
         */

        private String RequestId;
        private AssumedRoleUserBean AssumedRoleUser;
        private CredentialsBean Credentials;
        private String endpoint;
        private String bucket;
        private String chat_bucket;

        public String getRequestId() {
            return RequestId;
        }

        public void setRequestId(String RequestId) {
            this.RequestId = RequestId;
        }

        public AssumedRoleUserBean getAssumedRoleUser() {
            return AssumedRoleUser;
        }

        public void setAssumedRoleUser(AssumedRoleUserBean AssumedRoleUser) {
            this.AssumedRoleUser = AssumedRoleUser;
        }

        public CredentialsBean getCredentials() {
            return Credentials;
        }

        public void setCredentials(CredentialsBean Credentials) {
            this.Credentials = Credentials;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getChat_bucket() {
            return chat_bucket;
        }

        public void setChat_bucket(String chat_bucket) {
            this.chat_bucket = chat_bucket;
        }

        public static class AssumedRoleUserBean {
            /**
             * AssumedRoleId : 368101631399122462:client_name
             * Arn : acs:ram::1529031778181808:role/aliyunosstokengeneratorrole/client_name
             */

            private String AssumedRoleId;
            private String Arn;

            public String getAssumedRoleId() {
                return AssumedRoleId;
            }

            public void setAssumedRoleId(String AssumedRoleId) {
                this.AssumedRoleId = AssumedRoleId;
            }

            public String getArn() {
                return Arn;
            }

            public void setArn(String Arn) {
                this.Arn = Arn;
            }
        }

        public static class CredentialsBean {
            /**
             * AccessKeySecret : Htpff29oahozYi8s6DazvzXUTwvDk7VSbNcn85ryT2Wo
             * AccessKeyId : STS.NK55dPuRpfcYwnuucce69zP1G
             * Expiration : 2018-06-13T03:29:02Z
             * SecurityToken : CAISkQJ1q6Ft5B2yfSjIr4iAft7kmI1R0aGydUjEkWM2abkVlZWapTz2IH5EfXFpB+sftfU+nWpS6vgYlqJ4T55IQ1Dza8J148yiFqlwtM6T1fau5Jko1bebewHKeSeZsebWZ+LmNqS/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/EFA9MMRVv6F3UkYu1bPQx/ssQXGGLMPPK2SH7Qj3HXEVBjt3gf6wJ24r/txdaHuFj89ASnkL5N/N2ufcj9P5A8ZMdFPo3rjLAsRM3oyzVN7hVGzqBygZFf9C3P1tPnWAcBuk3fbbGNqYw+dVUjOvZgAd1NqPntiPt/offPkIf6zRlAO+xPWjjYXpqnxMbUwE41wWmFxtwagAF+YAW8xL9hr+ymSzjMCyp6KX4yfQBRCfNsWBZX1TxnK7RqA3kMgOaOg8JMrbV6w2oKP4LdVhCJOmKuYIe2hJTL9fJUzy3z2HbC4q2vcrf5jvSP/CHhzqM8r5AFfyphR0bg+RIXYJtYPDqdz17j/PwL7OzzaWd0m268ZAeH4Fti7w==
             */

            private String AccessKeySecret;
            private String AccessKeyId;
            private String Expiration;
            private String SecurityToken;

            public String getAccessKeySecret() {
                return AccessKeySecret;
            }

            public void setAccessKeySecret(String AccessKeySecret) {
                this.AccessKeySecret = AccessKeySecret;
            }

            public String getAccessKeyId() {
                return AccessKeyId;
            }

            public void setAccessKeyId(String AccessKeyId) {
                this.AccessKeyId = AccessKeyId;
            }

            public String getExpiration() {
                return Expiration;
            }

            public void setExpiration(String Expiration) {
                this.Expiration = Expiration;
            }

            public String getSecurityToken() {
                return SecurityToken;
            }

            public void setSecurityToken(String SecurityToken) {
                this.SecurityToken = SecurityToken;
            }
        }
    }
}
