import base64
import requests
from flask import Flask, jsonify, request

import smtplib
from email.mime.text import MIMEText
# 发送多种类型的邮件
from email.mime.multipart import MIMEMultipart


def get_access_token():
    url = 'https://aip.baidubce.com/oauth/2.0/token'
    data = {
        'grant_type': 'client_credentials',
        'client_id': '',  # API Key
        'client_secret': ''  # Secret Key
    }
    res = requests.post(url, data=data)
    res = res.json()
    # print(res)
    access_token = res['access_token']
    return access_token


app = Flask(__name__)


@app.route('/')
def home():
    return 'kiwi page'


@app.route('/get_image/<filename>')
def download(filename):
    filepath = 'uploads/' + filename
    with open(filepath, 'rb') as f:
        image_data = f.read()
        # 将图片数据编码为Base64字符串
        encoded_image = base64.b64encode(image_data).decode('utf-8')
        # 返回Base64字符串作为JSON响应
        return jsonify({'image': encoded_image})


@app.route('/get_text')
def get_text(txt):
    return txt


@app.route('/emailValidation', methods=['POST'])
def emailValidation():
    # 获取传入的params参数
    data = request.get_json()
    email = data['email']  # 获取email
    code = data['code']  # 获取code
    # email = data[:-6]
    # code = data[-6:]
    msg_from = ''  # 发送方邮箱
    passwd = ''     # 授权码
    # 接受方邮箱
    to = [email]
    # 设置邮件内容
    # MIMEMultipart类可以放任何内容
    msg = MIMEMultipart()
    content = "您的验证码为：" + code
    # 把内容加进去
    msg.attach(MIMEText(content, 'plain', 'utf-8'))

    # 设置邮件主题
    msg['Subject'] = "邮箱验证"

    # 发送方信息
    msg['From'] = msg_from

    # 开始发送
    # 通过SSL方式发送，服务器地址和端口
    s = smtplib.SMTP_SSL("smtp.qq.com", 465)
    # 登录邮箱
    s.login(msg_from, passwd)
    # 开始发送
    s.sendmail(msg_from, to, msg.as_string())
    res = "邮件发送成功"
    return res

if __name__ == '__main__':
    # file_path = 'uploads/1.jpg'
    # get_text('text')
    app.run(debug=True, host='0.0.0.0', port=5000)