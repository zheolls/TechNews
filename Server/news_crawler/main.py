# --*-- coding: utf-8 --*--
import os
import sys
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
sys.path.append(BASE_DIR)
import time
from news_crawler.crawler import crawl
from settings import *
from user_service.update_user_keyword import *

def start():
    cursor = mydb.cursor()
    cursor.execute("select webid,domain,name,category from web where webid<6")
    result = cursor.fetchall()

    for m in result:
        try:
            source=m[2]
            webid=m[0]
            category=m[3]
            url=m[1]
            print("######", source, category, url, "######")
            crawl(url, webid)
        except Exception as e:
            print(e)

if __name__ == '__main__':
    while True:
        start()
        time.sleep(20 * CRAWL_INTERVAL)
