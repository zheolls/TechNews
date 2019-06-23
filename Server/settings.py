import pymysql
from pytz import timezone
import mysql.connector

PAGE_NUM=20


mydb = pymysql.connect(
    host="localhost",
    user="root",
    passwd="password",
    database="technews",
)
mydb.autocommit=True

def db():
    mydb = pymysql.connect(
        host="localhost",
        user="root",
        passwd="password",
        database="technews",
    )
    mydb.autocommit = True
    return mydb

code={
    'email_exists':2,
    'phone_exists':1,
    'account_not_exists':2,
    'passwd_error':1,
    'signup_success':0,
    'signin_success':0,
    'server_error':3,
    'max_page':4,
    'send_news_success':8,
    'need_signin_again':9,
    'logout_success':0,
    'collect_success':0,
    'has_collect':1

}


# 新闻抓取频率(分钟)
CRAWL_INTERVAL = 60

# 用户关键词每日更新频率(分钟)
USER_KEYWORD_INTERVAL = 60*24

#用户关键词立即更新扫描频率（分钟）
USER_KEYWORD_INSTANT_INTERVAL=10

# TimeZone
UTC = timezone('UTC')
TIMEOUT = 60
cst_tz = timezone('Asia/Shanghai')
