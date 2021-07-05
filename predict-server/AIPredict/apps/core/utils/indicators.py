import numpy as np
from typing import List

def performance(scores:List) -> float:
    """Computes a bundle performance with a list of scores

    Args:
        scores (List): list of the bundle versions' scores

    Returns:
        float: the performance indicator, a float between 0 and 100
    """
    return np.round(100* np.mean(scores)/(1+np.std(scores)*len(scores)))