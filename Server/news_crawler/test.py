# --*-- coding: utf-8 --*--
import os
import sys
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
sys.path.append(BASE_DIR)
import re
import datetime
import requests
import time
import platform
import mysql.connector
from bs4 import BeautifulSoup
from settings import mydb



cursor = mydb.cursor()

headers = {
    'user-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36',
}


def get_driver():
    current_platform = platform.system()
    driver = None
    if current_platform == 'Darwin':
        from selenium import webdriver
        from selenium.webdriver.chrome.options import Options
        chrome_options = Options()
        chrome_options.add_argument('--headless')
        chrome_options.add_argument('--disable-gpu')
        driver = webdriver.Chrome('/Program Files (x86)/Google/Chrome/Application/chromedriver', chrome_options=chrome_options)
        driver.set_page_load_timeout(60)
        time.sleep(0.5)
    if current_platform == 'Linux':
        from selenium import webdriver
        from selenium.webdriver.chrome.options import Options
        chrome_options = Options()
        chrome_options.add_argument('--headless')
        chrome_options.add_argument('--disable-gpu')
        driver = webdriver.Chrome(chrome_options=chrome_options)
        driver.set_page_load_timeout(60)
        time.sleep(0.5)
    if current_platform == "Windows":
        from selenium import webdriver
        from selenium.webdriver.chrome.options import Options
        chrome_options = Options()
        chrome_options.add_argument('--headless')
        chrome_options.add_argument('--disable-gpu')
        driver = webdriver.Chrome('C:/Program Files (x86)/Google/Chrome/Application/chromedriver',options=chrome_options)
        driver.set_page_load_timeout(60)
        time.sleep(0.5)
    return driver


def get_image(driver, url):
    s = requests.Session()
    try:
        r = s.get(url, headers=headers)
        html_content = r.content
    except:
        time.sleep(6)
        driver.implicitly_wait(30)
        html_content = driver.page_source
    soup = BeautifulSoup(html_content, 'lxml')
    imgs= soup.findAll('img')
    if not imgs:
        return
    if len(imgs) >= 2:
        cover_img = imgs[1]
    else:
        cover_img = imgs[0]
    candidate_images = []
    print(imgs)
    for img in imgs:
        try:
            weight = int(img.get("wight").strip('px')) if img.get("wight") else 0
            height = int(img.get("height").strip('px')) if img.get("height") else 0
            if weight > 600 and height > 600:
                cover_img = img
                break
            if weight > 500 and height > 500:
                cover_img = img
                break
            if weight > 500 or height > 500:
                candidate_images.append(img)
        except:
            pass
    if candidate_images:
        cover_img =  candidate_images[0]
    if not cover_img:
        return

    cover_img_url = cover_img.get("src", None)
    return cover_img_url

def getdata(soup,webid):
    datalist=[]
    if webid==1:
        items = soup.find("div", attrs={"class": "lph-pageList index-pageList"})
        items = items.find('ul', attrs={"class": "clr"})
        list = items.find_all('li')
        for i in list:
            img=i.find("div",attrs={"class":"img"})
            j=img.find_all('a')[1]
            news_url = j.get('href')
            j=img.find("img")
            title=j.get("title")
            img_url=j.get("data-original")
            j=i.find('div',attrs={"class":"msg clr"})
            source=j.find('a').text.rstrip().replace('\n','')
            datalist.append((title,news_url,webid,source,img_url,datetime.datetime.utcnow()))

    elif webid==2:
        items = soup.find("div",id='content')
        items = items.find_all('div',attrs={'class':'figure flex-block'})
        for i in items:
            j=i.div.h2.a
            title=j.get('title')
            news_url='https:'+j.get('href')
            img=i.find('a',attrs={'class':'img'})
            if img:
                img = img.get('style')
                img = re.findall(r'//[0-9a-zA-Z._/=]+\)', img)
                img_url='https:'+img[0][:-1]
            else:
                img_url='http://zkres.myzaker.com/static/zaker_web2/img/logo_new.png'
            subtitle=i.find('div',attrs={'class':'subtitle'})
            subtitle=subtitle.find('span')
            source=subtitle.text
            datalist.append((title,news_url,webid,source,img_url,datetime.datetime.utcnow()))

    elif webid==22:
        items=soup.find('div',attrs={'class':'cate_list'}).ul
        items=items.find_all('li')
        for i in items:
            title=i.div.h2.a.text
            news_url=i.a.get('href')
            img_url='https:'+i.a.img.get('src')
            source='IT之家'
            datalist.append((title,news_url,webid,source,img_url,datetime.datetime.utcnow()))
    elif webid==3:
        items=soup.find_all('div',attrs={'class':'information-flow-item'})
        for i in items:
            img_url=i.find('img')
            if img_url:
                img_url=img_url.get('src')
            else:
                continue
            title=i.find('a',attrs={'class':'article-item-title weight-bold'}).text
            news_url='https://36kr.com'+i.find('a',attrs={'class':'article-item-title weight-bold'}).get('href')
            source=i.find('a',attrs={'class':'kr-flow-bar-author'}).text
            datalist.append((title,news_url,webid,source,img_url,datetime.datetime.utcnow()))

    elif webid==4:
        items=soup.find('div',attrs={'class':"ndi_main"})

        items=items.find_all('div',attrs={'class':'data_row news_article clearfix'})
        for i in items:
            news_url=i.a.get('href')
            img_url=i.img
            if img_url:
                img_url=img_url.get('src')
            else:
                img_url='http://static.ws.126.net/tech/2008/10/29/20081029155537efe3a.gif'
            title = i.find('div', attrs={'class': 'news_title'}).h3.a.text
            source='网易科技'
            datalist.append((title,news_url,webid,source,img_url,datetime.datetime.utcnow()))

    return  datalist


def crawl(url, webid):
    driver = get_driver()
    # try:
    driver.get(url)
    # time.sleep(6)
    driver.implicitly_wait(30)
    # driver.set_page_load_timeout(10)
    # driver.set_script_timeout(30)
    js_down = "var q=document.documentElement.scrollTop=100000"
    js_up = "var q=document.documentElement.scrollTop=0"
    for i in range(3):
        driver.execute_script(js_down)
        time.sleep(2)
        driver.execute_script(js_up)
        time.sleep(1)

    soup = BeautifulSoup(driver.page_source, 'lxml')
    for i in getdata(soup,webid):
        try:
            print(i)
            cursor.execute('update article set title=%s where url=%s',(i[0],i[1]))
            mydb.commit()
                # cursor.execute("insert into article(title,url,webid,source,imgurl,date) values (%s,%s,%s,%s,%s,%s)",i)
            # mydb.commit()
        except Exception as e:
            print(str(e))
    driver.quit()

crawl('https://tech.163.com/',4)