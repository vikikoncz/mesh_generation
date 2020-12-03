#!/usr/bin/gnuplot -p

set datafile commentschars "#!%"
unset key
set terminal postscript eps enhanced color solid  "Times-Roman" 20
#set decimalsign ','
set out "grid_function.eps"
#set xrange [0.193: 0.197]
#set xrange [0: 0.015]
set xrange [0.0348181175 : 0.0448181175]
#set yrange [0.189: 0.201]

#set title 'kcl_b=0.055 kcl_a=0.0 U=10.0V t 0.055 x 10.0V t=0.1'
#show title

set xlabel 'x'
set ylabel 'grid_function' offset 2


plot 'mesh.dat' u 2:3 w l lw 4
