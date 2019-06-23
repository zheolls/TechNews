# -*- coding: utf-8 -*-

import os
import sys
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__),".."))
sys.path.append(BASE_DIR)

from datetime import timedelta
from settings import mydb

def get_db():
    return mydb


def datetime_format(dt):
    dt = dt + timedelta(hours=8)
    return dt.strftime("%Y-%m-%d %H:%M")