from json import JSONEncoder
import numpy as np

class PredictResponseEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()

        if isinstance(obj, np.int64) or isinstance(obj, np.int32):
            return int(obj)
        
        if isinstance(obj, np.float64) or isinstance(obj, np.float32):
            return float(obj)

        return JSONEncoder.default(self, obj)

def parse_response(res):
    # removes key with none values from the prediction result dictionnary
    if "predictionNumeric" in res:
        npredict = len(res["predictionNumeric"])
    elif "predictionVector" in res:
        npredict = len(res["predictionVector"])
    else:
        npredict = len(res["predictionLabel"])
    res = {k:v for k,v in res.items() if not v is None}
    res = {"predictionList": [dict(zip(res,t)) for t in zip(*res.values())]}
    return res