<?php

namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;
use Carbon\Carbon;

class TodoResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return array
     */
    public function toArray($request)
    {
        return [
          'id' => $this->id,
          'taskName' => $this->taskName,
          'taskDes' => $this->taskDes,
          'startDate' => Carbon::parse($this->startDate)->format('Y/m/d'),
          'startTime' => Carbon::parse($this->startTime)->format('H:i'),
          'endDate' => Carbon::parse($this->endDate)->format('Y/m/d'),
          'endTime' => Carbon::parse($this->endTime)->format('H:i'),
          'priorityType' => $this->priorityType,
          'notifyFlag' => $this->notifyFlag,
          'compFlag' => $this->compFlag,
          'deleteFlag' => $this->deleteFlag,
        ];
    }
}
