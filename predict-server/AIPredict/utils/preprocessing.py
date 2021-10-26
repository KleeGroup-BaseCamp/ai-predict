import pandas as pd
import numpy as np
from AIPredict.utils.encoders import *
from AIPredict.utils.imputers import *

import logging

logger = logging.getLogger(__name__)
class FeatureEngineering(object):

    def __init__(self, data: pd.DataFrame, bundle={}) -> None:
        self.data = data.copy(deep=True)
        # The bundle is a dictionary which stores all the encoders and scalers (and their parameters) applied to a dataset.
        self.bundle = bundle

    def get_data(self) -> pd.DataFrame:
        return self.data

    def get_schema(self) -> pd.Index:
        return self.data.columns

    def get_bundle(self) -> dict:
        return self.bundle

    def add(self, column: str, encoderType: FeatureEncoder, imputerParams: str) -> bool:
        """This method adds and configure an encoder/scaler to a column of the attached dataset. It updates
        the feature engineering bundle with the column name, the encoder/scaler and its parameters.

        Args:
            column (str): name of the column
            encoderType (FeatureEncoder): encoder or scaler to apply (see encoders)

        Returns:
            bool: returns True is succeeded 
        """
        # check if the column name is a string and if it belongs to the dataset
        if column not in self.get_schema():
            return False
        # extract the column data from the dataset
        data = self.data[[column]]
        encoder = encoderType()

        self.bundle[column] = { "encoder": { encoder.get_label(): encoder.fit(data) } }

        if imputerParams is not None:
            if imputerParams not in ('mean', 'median', 'most_frequent'):
                imputer = SimpleImputer(strategy='constant', fill_value=imputerParams)
            else:
                imputer = SimpleImputer(strategy=imputerParams)
            self.bundle[column]["imputer"] = { imputer.get_label(): imputer.fit(data) }


        # updates the bundle content and computes the scaler/encoder parameters
        return True

    def transform(self) -> bool:
        """Apply all the transformations stored in the bundle dictionary to the dataset

        Returns:
            bool: returns True if succeeded
        """
        # modify all columns one by one
        for column in self.bundle:
            # apply the encoder/scaler to the columns
            if column in self.data.columns:
                imputers = self.bundle[column].get('imputer')
                if imputers is not None:
                    for imputerType in imputers:
                        # get the inputer parameters from the bundle
                        params = self.bundle[column]['imputer'][imputerType]
                        # initiate the encoder and set its parameters
                        imputer = pick_imputer_from_label(imputerType)()
                        imputer.set_params(params)
                        # transform the data
                        transformed_data = imputer.transform(self.data[[column]])
                        self.data[column] = transformed_data

                for encoderType in self.bundle[column]['encoder']:
                    # get the encoder/scaler's parameters from the bundle
                    params = self.bundle[column]['encoder'][encoderType]
                    # initiate the encoder and set its parameters
                    encoder = pick_encoder_from_label(encoderType)()
                    encoder.set_params(params)
                    # transform the data
                    transformed_data = encoder.transform(self.data[[column]])
                    # if the data transformation modifies the column (thus has the same dimensions), the method updates the columns
                    if transformed_data.shape == self.data[column].shape or transformed_data.shape == self.data[[column]].shape:
                        self.data[column] = transformed_data
                    # else, the method joins the result to the dateset
                    else:
                        self.data = self.data.reset_index().drop(columns=["index"])
                        transformed_data.columns = [
                            column + "_" + str(col) for col in transformed_data.columns]
                        self.data = self.data.join(transformed_data)
                        self.data = self.data.drop(columns=column)
        return True


encoders = {
    "MinMaxScaler": MinMaxScaler,
    "LabelEncoder": LabelEncoder,
    "OneHotEncoder": OneHotEncoder,
    "StandardScaler": StandardScaler,
}

def pick_encoder_from_label(label: str):
    return encoders[label]

imputers = {
    "SimpleImputer": SimpleImputer,
}

def pick_imputer_from_label(label: str):
    return imputers[label]
