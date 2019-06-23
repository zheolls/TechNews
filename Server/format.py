from flask import jsonify


def _format(item):

    users={'userid':0,'token':'','phone':'','email':'','nickname':''}
    error=''
    code=0
    if not isinstance(item,dict):
        return {'code':3,'error':error,'user':users}

    if 'code' in item:
        code=item['code']
    if 'user' in item:
       users=item['user']
    else:
        if 'id' in item:
            users['userid']=item['id']
        if 'email' in item:
            users['email']=item['email']
        if 'nickname' in item:
            users['nickname'] = item['nickname']
        if 'phone' in item:
            users['phone'] = item['phone']
        if 'token' in item:
            users['token'] = item['token']
    if 'error' in item:
        error=item['error']

    result={'code':code,'error':error,'user':users}

    return jsonify(result)

def _format_video(item):
    videos={'videoid':0,'url':'','source':'','imgurl':'','date':'','authorimgurl':'','fever':0,'title':''}
    error=''
    code=0
    if not isinstance(item,dict):
        return {'code':3,'error':error,'videos':[videos]}

    if 'code' in item:
        code=item['code']
    if 'videos' in item:
        videos=item['videos']
    else:
        if 'articleid' in item:
            videos['articleid']=item['articleid']
        if 'url' in item:
            videos['url']=item['url']
        if 'source' in item:
            videos['source'] = item['source']
        if 'imgurl' in item:
            videos['imgurl'] = item['imgurl']
        if 'date' in item:
            videos['date'] = item['date']
        if 'authorimgurl' in item:
            videos['authorimgurl'] = item['authorimgurl']
        if 'fever' in item:
            videos['fever'] = item['fever']
        if 'title' in item:
            videos['title'] = item['title']
        videos=[videos]
    if 'error' in item:
        error=item['error']

    result={'code':code,'error':error,'videos':videos}

    return jsonify(result)


def _format_news(item):
    news = {'articleid': 0, 'url': '', 'source': '', 'imgurl': '', 'date': '', 'webid': 0, 'fever': 0, 'title': ''}
    error = ''
    code = 0
    if not isinstance(item, dict):
        return {'code': 3, 'error': error, 'news': [news]}

    if 'code' in item:
        code = item['code']
    if 'news' in item:
        news = item['news']
    else:
        if 'articleid' in item:
            news['articleid'] = item['articleid']
        if 'url' in item:
            news['url'] = item['url']
        if 'source' in item:
            news['source'] = item['source']
        if 'imgurl' in item:
            news['imgurl'] = item['imgurl']
        if 'date' in item:
            news['date'] = item['date']
        if 'webid' in item:
            news['webid'] = item['webid']
        if 'fever' in item:
            news['fever'] = item['fever']
        if 'title' in item:
            news['title'] = item['title']
        news=[news]
    if 'error' in item:
        error = item['error']
    result = {'code': code, 'error': error, 'news': news}

    return jsonify(result)