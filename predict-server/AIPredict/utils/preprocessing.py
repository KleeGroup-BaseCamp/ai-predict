import pandas as pd
from AIPredict.utils.encoders import *


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

    def add(self, column: str, encoderType: FeatureEncoder) -> bool:
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
        # updates the bundle content and computes the scaler/encoder parameters
        self.bundle[column] = {encoder.get_label(): encoder.fit(data)}
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
                for encoderType in self.bundle[column]:
                    # get the encoder/scaler's parameters from the bundle
                    params = self.bundle[column][encoderType]
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
