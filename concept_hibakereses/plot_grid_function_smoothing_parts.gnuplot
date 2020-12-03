#!/usr/bin/gnuplot -p

set datafile commentschars "#!%"
unset key
set terminal postscript eps enhanced color solid  "Times-Roman" 20
#set decimalsign ','
set out "grid_function_smoothing_parts.eps"
#set xrange [0.193: 0.197]
#set xrange [0: 0.015]
#set xrange [0: 0.015]
set xrange [0.0348181175 : 0.0448181175]
#set yrange [0.189: 0.201]

#set title 'kcl_b=0.055 kcl_a=0.0 U=10.0V t 0.055 x 10.0V t=0.1'
#show title

set xlabel 'x'
set ylabel 'grid_function' offset 2

g(x) = -81.2973679905 * x *x + 7.2996899809*x + 0.0167451235

f(x) = -2.0898067845694956E-5 * exp(-598.1414211254*(x-0.0448181175)) + 0.1806253165


plot f(x),\
     g(x)


#plot sin(x)
