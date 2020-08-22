<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Todo extends Model
{
  protected $fillable = [
    'taskName',
    'taskDes',
    'startDate',
    'startTime',
    'endDate',
    'endTime',
    'priorityType',
    'notifyFlag',
    'compFlag',
    'deleteFlag',
];
}
