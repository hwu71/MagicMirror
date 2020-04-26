xdotool windowactivate --sync $(xdotool search --class electron | awk 'FNR == 3 {print}') key ctrl+r
