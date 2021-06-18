import numpy as np
from typing import List

def performance(scores:List) -> float:
    return np.round(100* np.mean(scores)/(1+np.std(scores)*len(scores)))