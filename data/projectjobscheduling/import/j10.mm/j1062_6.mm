************************************************************************
file with basedata            : mm62_.bas
initial value random generator: 719962215
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  12
horizon                       :  82
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     10      0       19        4       19
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          1           7
   3        3          2           6  11
   4        3          3           5   7   9
   5        3          1          10
   6        3          2           8  10
   7        3          2          10  11
   8        3          1           9
   9        3          1          12
  10        3          1          12
  11        3          1          12
  12        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     3       4    9   10    8
         2     8       3    8    7    7
         3    10       2    5    5    7
  3      1     2       7    6    7    8
         2     3       6    6    6    5
         3     8       6    5    3    3
  4      1     2       7   10    5    6
         2     2       5   10    7    6
         3     5       1    8    3    6
  5      1     2       7    3    3    5
         2     4       6    3    3    4
         3    10       6    3    2    2
  6      1     7       6   10    9    7
         2     8       5    9    8    7
         3     9       3    8    8    7
  7      1     4       8    7    9    8
         2     6       5    6    9    4
         3     6       8    5    9    4
  8      1     5       5   10    7    7
         2     7       4    7    6    7
         3    10       3    7    5    6
  9      1     5       9    3    2   10
         2     7       6    3    1    9
         3    10       3    3    1    6
 10      1     3       5    7   10    8
         2     5       4    6    9    8
         3     7       3    5    8    7
 11      1     2       4    8    4    8
         2     3       4    5    3    7
         3     7       3    3    3    6
 12      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   21   25   58   65
************************************************************************
