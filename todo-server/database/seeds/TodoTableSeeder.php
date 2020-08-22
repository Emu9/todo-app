<?php

use Illuminate\Database\Seeder;

class TodoTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
      DB::table('todo')->insert([
        ['taskName' => '全体共通',
        'taskDes' => 'ベランダ',
        'startDate' => '2020-08-09',
        'startTime' => '12:21:00',
        'endDate' => '2020-08-09',
        'endTime' => '13:31:00',
        'priorityType' => 1,
        'notifyFlag' => 0,
        'compFlag' => 0,
        'deleteFlag' => 0],
    ]);
    }
}
