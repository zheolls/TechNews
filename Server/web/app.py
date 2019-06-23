# -*- coding: utf-8 -*-

import os
import sys
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
sys.path.append(BASE_DIR)

from flask import flash
from flask import redirect
from flask import render_template
from flask import session
from flask import url_for
from user_service.videos import *
from user_service.sign import *
from user_service.news_send import get_news
from user_service.article import *
from flask import request
from flask import Flask, jsonify
from web.utils import get_db
from web.utils import datetime_format
from format import _format,_format_news,_format_video
account={
    'USERNAME':'root',
    'PASSWORD':'password'
}

list=['0616cec2f6e44257adc634321abf6ed3']
app = Flask(__name__)
# app.config.from_object('settings')
app.secret_key='123456'
db = get_db()
cursor=db.cursor()

@app.route('/api/v1/news')
def get_news_manager():
    page = int(request.args.get('page', 0))
    limit = int(request.args.get('limit', 10))
    sql='select articleid,title,url,imgurl,source,date from article LIMIT %s'
    news = cursor.execute(sql,(limit,))
    news=cursor.fetchall()

    # import json
    # from bson import json_util
    # docs_list = list(news)
    # return json.dumps(docs_list, default=json_util.default)

    data = []
    for n in news:
        item = {
            'id': str(n[0]),
            'title': n[1],
            'url': n[2],
            'image': n[3],
            'source': n[4],
            'created_at': datetime_format(n[5]),
        }

        data.append(item)
    return jsonify(data)

@app.route('/api/signup',methods=['GET','POST'])
def signup():
    if request.method == 'POST':
        item={
            'email':request.form['email'],
            'password':request.form['password'],
            'phone':request.form['phone'],
            'nickname':request.form['nickname']
        }
        result=user_signup(item)
        print(result)
        if result['code']==code['signin_success']:
            session[result['token']]=True
            session.permanent = True
            list.append(result['token'])

        return _format(result)
    return _format({'code':code['server_error']})

@app.route('/api/news',methods=['GET','POST'])
def getnews():
    if request.method == 'POST':
        item={
            'type':request.form['type'],
            'userid': int(request.form['userid']),
            'page':int(request.form['page']),
            'token':request.form['token'],
            'date':(request.form['date']),
            'webid':int(request.form['webid'])
        }
        print(item)
        if item['type']!='signedin' or item['token'] in list:
            return _format_news(get_news(item))
        elif item['type']=='signedin':
            print({'code': code['need_signin_again']})
            return _format_news({'code': code['need_signin_again']})

    return _format_news({'code':code['server_error']})

@app.route('/api/videos',methods=['GET','POST'])
def getvideos():
    if request.method == 'POST':
        item={
            # 'userid': int(request.form['userid']),
            'page':int(request.form['page']),
            # 'token':request.form['token'],
            'date':(request.form['date']),
            # 'webid':int(request.form['webid'])
        }
        print(item)
        result=get_videos(item)
        print({'code':result['code']})
        return _format_video(result)
        # print(session.get(item['token']))
        # if session.get(item['token']) or item['type']!='signedin' or item['token'] in list:
        #     return jsonify(get_news(item))
        # elif item['type']=='signedin':
            # return jsonify({'code': code['need_signin_again']})

    return _format_video({'code':code['server_error']})

# @app.route('/api/news/<type>')
# def getnews(type):

@app.route('/api/articleclick',methods=['GET','POST'])
def click():
    try:
        if request.method == 'POST':
            item={
                'token':request.form['token'],
                'userid':int(request.form['userid']),
                'articleid':int(request.form['articleid'])
            }
            print(item)
            if session.get(item['token']) or item['token'] in list:
                click_article(item)
                update_user_click(item)
            else:
                update_user_click(item)

                click_article(item)
        return _format({'code':0})
    except Exception as e:
        print(e)
        return _format({'code':1})

@app.route('/api/track',methods=['GET','POST'])
def gettrack():
    try:
        if request.method == 'POST':
            item={
                'token':request.form['token'],
                'userid':int(request.form['userid']),
                'page':int(request.form['page']),
                'date':request.form['date']
            }
            print(item)
            if item['token'] in list:
                return _format_news(get_track(item))
            else:
                return _format_news({'code':code['need_signin_again']})
    except Exception as e:
        print(e)
        return _format_news({'code':code['server_error']})

@app.route('/api/search',methods=['GET','POST'])
def searcharticle():
    try:
        if request.method == 'POST':
            item={
                'token':request.form['token'],
                'userid':int(request.form['userid']),
                'page':int(request.form['page']),
                'date':request.form['date'],
                'key':request.form['key']
            }
            print(item)
            return _format_news(search_article(item))
    except Exception as e:
        print(e)
        return _format_news({'code':code['server_error']})

@app.route('/api/collect',methods=['GET','POST'])
def getcollect():
    try:
        if request.method == 'POST':
            item={
                'token':request.form['token'],
                'userid':int(request.form['userid']),
                'page':int(request.form['page']),
                'date':request.form['date']
            }
            print(item)
            if item['token'] in list:
                return _format_news(get_collect_article(item))
            else:
                return _format_news({'code':code['need_signin_again']})
    except Exception as e:
        print(e)
        return _format({'code':code['server_error']})


@app.route('/api/collectarticle',methods=['GET','POST'])
def collectarticle():
    try:
        if request.method == 'POST':
            item={
                'token':request.form['token'],
                'userid':int(request.form['userid']),
                'articleid':int(request.form['articleid']),
                "check":int(request.form["check"])
            }
            print(item)
            if session.get(item['token']) or item['token'] in list:
                return _format(colloct_article(item))
            else:
                return _format({'code':code['need_signin_again']})
    except Exception as e:
        print(e)
        return _format({'code':code['server_error']})


@app.route('/api/login',methods=['GET','POST'])
def adnroid_login():
    if request.method == 'POST':
        item={
            'email':request.form['email'],
            'password':request.form['password']
        }
        print(item)
        result=user_signin(item)
        print(result)
        if result['code']==code['signin_success']:
            session[result['token']]=True
            session.permanent = True
            list.append(result['token'])

        return _format(result)
    return _format({'code':code['server_error']})

@app.route('/api/logout',methods=['GET','POST'])
def adnroid_logout():
    if request.method == 'POST':
        item={
            'token':request.form['token'],
        }
        session.pop(item['token'],None)
        if item['token'] in list:
            list.remove(item['token'])
        return _format({'code':code['logout_success']})
    return _format({'code':code['server_error']})

@app.route('/api/changeuserinfo',methods=['GET','POST'])
def changeinfo():
    if request.method == 'POST':
        item={
            'userid':int(request.form['userid']),
            'phone':request.form['phone'],
            'nickname':request.form['nickname'],
            'token':request.form['token']
        }
        result=change_user_info(item)
        return _format(result)
    return _format({'code':code['server_error']})

@app.route('/login', methods=['GET', 'POST'])
def login():
    error = None
    if request.method == 'POST':
        if request.form['username'] != account['USERNAME']:
            error = 'Invalid username'
        elif request.form['password'] != account['PASSWORD']:
            error = 'Invalid password'
        else:
            session['logged_in'] = True
            flash('You were logged in')
            return redirect(url_for('index'))
    return render_template('login.html', error=error)


@app.route('/logout')
def logout():
    session.pop('logged_in', None)
    flash('You were logged out')
    return redirect(url_for('index'))


@app.route('/')
def index():
    page = int(request.args.get('page', 0))
    limit = int(request.args.get('limit', 100))

    sql='select articleid,title,url,imgurl,source,date from article LIMIT %s'
    cursor.execute(sql,(limit,))
    news=cursor.fetchall()
    # import json
    # from bson import json_util
    # docs_list = list(news)
    # return json.dumps(docs_list, default=json_util.default)

    entries = []
    for n in news:
        item = {
            'id': str(n[0]),
            'title': n[1],
            'url': n[2],
            'image': n[3],
            'source': n[4],
            'created_at': datetime_format(n[5]),
        }

        entries.append(item)
        # random.shuffle(entries)

    return render_template('show_entries.html', entries=entries)






if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
