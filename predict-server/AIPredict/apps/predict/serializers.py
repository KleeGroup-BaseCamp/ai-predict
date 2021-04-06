from rest_framework import serializers, status
from AIPredict.apps.predict.models import Bundle

from rest_framework.response import Response

class BundleSerializer(serializers.ModelSerializer):

    class Meta:
            model = Bundle
            fields = ['id', 'name', 'activated', 'version']
    
    def create(self, validated_data):
        return Bundle.objects.create(**validated_data)