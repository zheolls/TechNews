from settings import *
import uuid
mydb.ping(reconnect=True)

def user_signup(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql='select * from user where email=%s'
        cursor.execute(sql,(item['email'],))
        result=cursor.fetchall()
        if  len(result)==0:
            sql = 'select * from user where phone= %s'
            cursor.execute(sql, (item['phone'],))
            if len(cursor.fetchall())==0:
                sql = 'insert into user(email, password, phone, nickname) VALUES (%s,%s,%s,%s)'
                cursor.execute(sql, (item['email'],item['password'],item['phone'],item['nickname']))
                mydb.commit()
                sql='select userid from user where email=%s'
                cursor.execute(sql,(item['email'],))
                result=cursor.fetchall()[0][0]
                mydb.close()
                return {'code':code['signup_success'],'id':result,'token':uuid.uuid4().hex}
            else:
                return {'code':code['phone_exists']}
        else:
            return {'code':code['email_exists']}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}

def user_signin(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql='select userid,password,nickname,phone,email from user where email=%s'
        cursor.execute(sql,(item['email'],))
        result=cursor.fetchall()
        if len(result):
            if result[0][1]==item['password']:
                return {'code':code['signin_success'],
                        'nickname':result[0][2],
                        'id':result[0][0],
                        'phone':result[0][3],
                        'token':uuid.uuid4().hex,
                        'email':result[0][4]
                        }
            else:
                return {'code':code['passwd_error']}
        else:
            return {'code':code['account_not_exists']}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}
def change_user_info(item):
    try:
        mydb=db()
        cursor=mydb.cursor()
        sql = 'select userid,phone from user where phone= %s'
        cursor.execute(sql, (item['phone'],))
        result=cursor.fetchall()
        if len(result) == 0 or (result[0][0]==item['userid']):
            sql = 'update  user set nickname=%s,phone=%s where userid=%s'
            cursor.execute(sql, (item['nickname'],item['phone'],item['userid'],))
            mydb.commit()
            return {'code': 0}
        else:
            return {'code': code['phone_exists']}
    except Exception as e:
        print(e)
        return {'code':code['server_error']}