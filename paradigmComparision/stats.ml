(* stats.ml – Basic statistics in OCaml (functional paradigm) *)

(* ── Mean ── *)
let mean lst =
  let sum   = List.fold_left ( +. ) 0.0 (List.map float_of_int lst) in
  let count = float_of_int (List.length lst) in
  sum /. count

(* ── Median ── *)
let median lst =
  let sorted = List.sort compare lst in
  let n      = List.length sorted in
  let nth i  = List.nth sorted i in
  if n mod 2 = 1
  then float_of_int (nth (n / 2))
  else (float_of_int (nth (n/2 - 1)) +. float_of_int (nth (n/2))) /. 2.0

(* ── Mode ── *)
(* Build a frequency map using fold, then find the maximum frequency,
   then collect every value that hits that frequency.                  *)
let mode lst =
  (* Step 1: frequency map as an association list *)
  let freq_map =
    List.fold_left
      (fun acc x ->
        match List.assoc_opt x acc with
        | Some c -> (x, c + 1) :: List.remove_assoc x acc
        | None   -> (x, 1) :: acc)
      [] lst
  in
  (* Step 2: maximum frequency *)
  let max_freq =
    List.fold_left (fun acc (_, c) -> max acc c) 0 freq_map
  in
  (* Step 3: all keys with the max frequency, sorted for determinism *)
  let modes =
    freq_map
    |> List.filter (fun (_, c) -> c = max_freq)
    |> List.map fst
    |> List.sort compare
  in
  modes

(* ── Pretty-print helpers ── *)
let print_int_list lst =
  let strs = List.map string_of_int lst in
  Printf.printf "[%s]\n" (String.concat ", " strs)

let run label data =
  Printf.printf "\n--- %s ---\n" label;
  Printf.printf "Data:   "; print_int_list data;
  Printf.printf "Mean:   %.2f\n" (mean data);
  Printf.printf "Median: %.2f\n" (median data);
  Printf.printf "Mode:   "; print_int_list (mode data)

(* ── Entry point ── *)
let () =
  Printf.printf "=== OCaml Statistics Calculator (Functional) ===\n";
  run "Example 1" [4; 1; 2; 2; 3; 5; 3; 3; 7; 1];
  run "Example 2" [10; 20; 20; 30; 40; 40; 40; 50]
