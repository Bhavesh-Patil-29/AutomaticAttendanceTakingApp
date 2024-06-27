
from deepface import DeepFace
import firebase_admin
from firebase_admin import credentials,storage,firestore
import numpy as np
import cv2
import base64
import openpyxl
import pyrebase
import smtplib
import os
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.application import MIMEApplication
from datetime import datetime



config = {
  "apiKey": "AIzaSyAQQLmDWFmPmEF-yVpEx_yHHUXK2GcbQ3I",
  "authDomain": "my-application3-5a2a4.firebaseapp.com",
  "databaseURL": "https://my-application3-5a2a4-default-rtdb.firebaseio.com",
  "projectId": "my-application3-5a2a4",
  "storageBucket": "my-application3-5a2a4.appspot.com",
  "messagingSenderId": "732150410189",
  "appId": "1:732150410189:web:b25ff725347206cb243284",
  "measurementId": "G-KXV92YP8VY",
  "serviceAccount": "key.json",
  "databaseURL":"https://my-application3-5a2a4-default-rtdb.firebaseio.com/"
};

def main1():
    firebase=pyrebase.initialize_app(config)
    storage1=firebase.storage()

    r=False
    cred = credentials.Certificate("./key.json")
    app = firebase_admin.initialize_app(cred,{'storageBucket' : 'my-application3-5a2a4.appspot.com'})

    bucket = storage.bucket()
    db = firestore.client()
    blob = bucket.get_blob("image")
    arr = np.frombuffer(blob.download_as_string(),np.uint8)

    img=cv2.imdecode(arr,cv2.COLOR_BGR2BGR555)
    imgResized=cv2.resize(img,(800,800))

    detector = cv2.CascadeClassifier("haarcascade_frontalface_default.xml")
    gray= cv2.cvtColor(imgResized,cv2.COLOR_BGR2GRAY)

    faces=detector.detectMultiScale(gray,scaleFactor = 1.1, minNeighbors = 1, minSize = (5,5), maxSize = (200,200))
    count=0


    res=db.collection("resources").document("res").get()
    tid = res.to_dict()["teacherid"]
    yr = res.to_dict()["year"]
    fyr = res.to_dict()["fyr"]
    sub = res.to_dict()["Sub"]

    teacher=db.collection("Teachers").document("t"+str(tid)).get()
    teacher_email = teacher.to_dict()["Email"]

    db.collection("Students"+str(tid)+str(yr)).document("images").set({})


    for(x,y,w,h) in faces:
        face = imgResized[y:y+h,x:x+w]
        count+=1
        cv2.imwrite("src/main/python/" + str(count) + ".jpg", face)
        img = cv2.imread(str(count)+".jpg")

        with open(str(count)+".jpg","rb") as image_file:
            encoded_string = base64.b64encode(image_file.read())

        data = {'img'+str(count):encoded_string}
        db.collection("Students"+str(tid)+str(yr)).document("images").set(data,merge=True)


    cdate = datetime.today().strftime('%d-%m-%Y')
    ctime = datetime.now().strftime('%H:%M')

    abc=db.collection("Students"+str(tid)+str(yr)).get()

    workbook = openpyxl.Workbook()
    worksheet = workbook.active
    worksheet['A1'] = 'YEAR'
    worksheet['B1'] = str(fyr)
    worksheet['A2'] = 'SUBJECT'
    worksheet['B2'] = str(sub)
    worksheet['A3'] = 'DATE'
    worksheet['B3'] = cdate
    worksheet['A4'] = 'TIME'
    worksheet['B4'] = ctime
    worksheet['A5'] = 'ROLL NO'
    worksheet['B5'] = 'STATUS'
    counter=1
    c=1
    no=0
    l=len(abc)
    y=0
    print("Done5")
    docs = db.collection("Students" + str(tid) + str(yr))

    while(y!=(l-1)):

        docs1 = docs.document("std" + str(c)).get()
        c=c+1

        if docs1.exists:
            y=y+1
            roll_ref = docs1.to_dict()['Name']
            worksheet['A'+str(counter+5)] = roll_ref
            counter=counter+1

    counter=1
    workbook.save('present.xlsx')
    count=1
    z=0
    while(z!=(l-1)):

        docs=db.collection("Students"+str(tid)+str(yr)).document("std"+str(count)).get()

        if docs.exists:
            z=z+1
            nf = docs.to_dict()
            nfields = len(nf)
            print(nfields-3)
            ab=0
            for x in range(nfields-3):
                doc_ref = docs.to_dict()["Student Image"+str(ab)]
                ab=ab+1
                decoded_data = base64.b64decode(doc_ref)
                arr = np.frombuffer(decoded_data,np.uint8)
                img=cv2.imdecode(arr,cv2.COLOR_BGR2BGR555)
                img2=cv2.resize(img,(400,400))


                xyz=db.collection("Students"+str(tid)+str(yr)).document('images').get()
                xyz_dict=xyz.to_dict()
                num_fields = len(xyz_dict)
                a=0

                for x in range(num_fields):
                    a = a + 1

                    doc_ref1 = xyz.to_dict()["img"+str(a)]

                    if xyz.exists:
                        decoded_data1 = base64.b64decode(doc_ref1)

                    if blob is None:
                        print("blob not found")
                    else:
                        arr1 = np.frombuffer(decoded_data1, np.uint8)

                        img = cv2.imdecode(arr1, cv2.COLOR_BGR2BGR555)
                        imgResized = cv2.resize(img, (200, 200))

                        try:

                            result = DeepFace.verify(imgResized,img2)
                            r=result['verified']

                            if result['verified']:
                                worksheet['B'+str(counter+5)] = 'P'

                                print("true")
                                break


                        except ValueError:
                            worksheet['B' + str(counter + 5)] = 'A'


                if r:
                    r=False
                    break

            counter = counter+1

        else:
            print("Data of "+str(count)+" does not exists")

        count = count + 1

    workbook.save('present.xlsx')

    sender_email = 'bsp335059@gmail.com'
    sender_password = 'haepmwrgfrbjbkqv'


    receiver_email = teacher_email
    email_subject = 'Excel file attachment'
    email_body = 'Please find the attached Excel file.'

    message = MIMEMultipart()
    message['From'] = sender_email
    message['To'] = receiver_email
    message['Subject'] = email_subjectg
    message.attach(MIMEText(email_body, 'plain'))

    file_path = 'present.xlsx'
    with open(file_path, 'rb') as file:
        attachment = MIMEApplication(file.read(), _subtype='xlsx')
        attachment.add_header('Content-Disposition', 'attachment', filename=os.path.basename(file_path))
        message.attach(attachment)

    smtp_server = 'smtp.gmail.com'
    smtp_port = 587
    smtp_connection = smtplib.SMTP(smtp_server, smtp_port)
    smtp_connection.starttls()
    smtp_connection.login(sender_email, sender_password)
    smtp_connection.sendmail(sender_email, receiver_email, message.as_string())
    smtp_connection.quit()
    ret='Email sent successfully.'
    return ret


