#!/usr/bin/python

import os
import cgitb
cgitb.enable()
import re
import cgi
import urlparse

# must print this line, as a divider of head & body
print

# get url host addr
arguments = cgi.FieldStorage()
# print arguments
request_http_url = arguments.getvalue("request")
HOST_ADDR = urlparse.urlparse("http://" +request_http_url).hostname # add http prifex for urlparse

# global value
resource_dir_path="../resource/"
content_str=""

# CONST VALUE
SNAP_FILENAME="snap.jpg"
STAT_FILE_PIXFEX="stat."
STAT_NULL = "null"
STAT_NEW= "new"
STAT_READY= "ready"
STAT_RELEASE= "release"

def getStatusDescriptStr(path_to_dir, dir_name, list_of_content, ):
    """
    get the stat descript string: new, ready, release
    """
    stat_mark_count = 0;
    for var in list_of_content:
        if (var.find(STAT_FILE_PIXFEX) >= 0):
            if (var.endswith(STAT_NEW)) :
                return STAT_NEW
            elif (var.endswith(STAT_READY)):
                return STAT_READY
            elif (var.endswith(STAT_RELEASE)):
                return STAT_RELEASE
            else :
                return var
    return STAT_NULL


def getDirDiscriptValueAsStr(build_name, resource_dirname, umeng_appkey, package_name, app_name, baidu_appkey,  ):
    """
    print dir status
    """

    _tmp=""
    # id
    _tmp += "<td>" + resource_dirname + "</td>\n"
    # build name
    _tmp += "<td>" + build_name + "</td>\n"
    # app name
    _tmp+="<td>" + app_name + "</td>\n"
    # pkg name
    _tmp+="<td>" + package_name + "</td>\n"

    dir_content = os.listdir(os.path.join(resource_dir_path, resource_dirname))
    # snap url
    _tmp += "<td>"
    _found_snap = False;
    for var in dir_content:
        if var.find(SNAP_FILENAME) >= 0 :
            _found_snap = True;
            _tmp += "<img src=\"" + os.path.join(resource_dir_path, resource_dirname, SNAP_FILENAME) + "\"/>"
            break;
    if (not _found_snap):
        _tmp += "null"
    _tmp += "</td>\n"

    # status
    stat = getStatusDescriptStr((os.path.join(resource_dir_path, resource_dirname)), resource_dirname, dir_content)
    _tmp+="<td>" + stat + "</td>\n"

    # opera
    # _tmp += "<td>" + "<a href=\"cgi-bin/operator.cgi" + \
    #     "?" + "resource_id=" + resource_dirname + \
    #     "&" + "stat=" + stat + \
    #     "&" + "name=" + app_name + \
    #     "\">Config</a>" + "</td>\n"
    _tmp += "<td>" + "<a href=\"ftp://" + HOST_ADDR + "/" + resource_dirname \
        + "\">FTP Address</a>" + "</td>\n"
    return _tmp

import socket
import sys

def getContentFromSocket():
    """
    """

    retval = ""
    HOST, PORT = "localhost", 9999
    data = "get_lists"

    # Create a socket (SOCK_STREAM means a TCP socket)
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    try:
        # Connect to server and send data
        sock.connect((HOST, PORT))
        sock.sendall(data)

        # Receive data from the server and shut down
        received = sock.recv(4096)
        while (len(received) > 0):
            retval += received
            received = sock.recv(4096)

    finally:
        sock.close()
        
    # print "Sent:     {}".format(data)
    # print "Received: {}".format(received)
    return retval



import commands

curl_command="curl -s http://localhost:7777"

content_str = commands.getstatusoutput(curl_command)[1]

print content_str



