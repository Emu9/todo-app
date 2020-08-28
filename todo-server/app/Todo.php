<?php

namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Todo extends Model
{
  use SoftDeletes;
  protected $fillable = [
    'taskName',
    'taskDes',
    'startDate',
    'startTime',
    'endDate',
    'endTime',
    'priorityType',
    'compFlag',
  ];
  protected $dates = ['deleted_at'];
}