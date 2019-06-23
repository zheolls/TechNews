import time

from video_crawler.crawler import video_crawler
from settings import *

mydb=db()
cursor = mydb.cursor()
cursor.execute("select webid,domain,name,category from web where webid>5")
result = cursor.fetchall()
mydb.close()

def start():
    for m in result:
        # try:
            source=m[2]
            webid=m[0]
            category=m[3]
            url=m[1]
            print("######", source, category, url, "######")
            video_crawler(url, webid)
        # except Exception as e:
        #     print(e)

if __name__ == '__main__':
    while True:
        start()
        time.sleep(20 * CRAWL_INTERVAL)