__config() -> {'scope' -> 'global'};

__spawn_players() -> (
   data = load_app_data();
   if (data && data:'players',
      data = parse_nbt(data:'players');
      for (data,
         for([
            str('player %s spawn at %f %f %f facing %f %f in %s',
                _:'name', _:'x', _:'y', _:'z', _:'yaw', _:'pitch', _:'dim'),
            str('gamemode %s %s', _:'gm', _:'name')
         ],
            logger('warn', _);
            run(_);
         );
         if (player(_:'name'),
            modify(player(_:'name'), 'flying', _:'fly')
         )
      )
   );
);

__on_server_starts() -> (
  task('__spawn_players');
);

__on_server_shuts_down() -> (
   data = nbt('{players:[]}');
   saved = [];            // initialize an empty list for saved names
   players_list = [];     // initialize an empty list to hold fake players
   
   for (filter(player('all'), _~'player_type' == 'fake'),
      pdata = nbt('{}');
      pdata:'name'  = _~'name';
      pdata:'dim'   = _~'dimension';
      pdata:'x'     = _~'x';
      pdata:'y'     = _~'y';
      pdata:'z'     = _~'z';
      pdata:'yaw'   = _~'yaw';
      pdata:'pitch' = _~'pitch';
      pdata:'gm'    = _~'gamemode';
      pdata:'fly'   = _~'flying';
      
      // Use append() to add the current fake player's data to the players_list
      players_list = append(players_list, pdata);
      // Also append the player's name to the saved list
      saved = append(saved, _~'name');
   );
   
   data:'players' = players_list;
   store_app_data(data);
   if (saved,
      logger('warn', 'saved ' + str(saved) + ' for next startup')
   );
);
