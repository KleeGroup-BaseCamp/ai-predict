# syntax=docker/dockerfile:1
FROM python:3.6

ENV PYTHONUNBUFFERED=1

WORKDIR /code

COPY requirements-main.txt /code/
COPY requirements-ml.txt /code/
COPY requirements-deepml.txt /code/

RUN pip install -r requirements-main.txt
RUN pip install -r requirements-ml.txt
RUN pip install -r requirements-deepml.txt

COPY . /code/

CMD python ./scripts/manage.py runserver 0.0.0.0:8000