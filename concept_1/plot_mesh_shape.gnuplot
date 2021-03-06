#!/usr/bin/gnuplot -p

set datafile commentschars "#!%"
unset key
set terminal postscript eps enhanced color solid  "Times-Roman" 20
#set decimalsign ','
set out "mesh_dens.eps"
set xrange [0.1: 0.3]

#set title 'kcl_b=0.055 kcl_a=0.0 U=10.0V t 0.055 x 10.0V t=0.1'
#show title

set xlabel 'x'
set ylabel 'mesh_dens' offset 2


plot 'mesh_denstity_1.dat' u 2:3 w l lw 4
