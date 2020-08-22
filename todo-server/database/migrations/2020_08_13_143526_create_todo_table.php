<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateTodoTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('todo', function (Blueprint $table) {
            $table->id()->comment('ID');
            $table->string('taskName', 10)->comment('タスク名');
            $table->string('taskDes', 50)->nullable()->comment('タスク詳細');
            $table->date('startDate')->comment('開始日');
            $table->time('startTime')->comment('開始時間');
            $table->date('endDate')->comment('終了日');
            $table->time('endTime')->comment('終了時間');
            $table->integer('priorityType')->default(0)->comment('優先度区分');
            $table->integer('notifyFlag')->default(0)->comment('通知フラグ');
            $table->integer('compFlag')->default(0)->comment('終了フラグ');
            $table->integer('deleteFlag')->default(0)->comment('論理削除フラグ');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('todo');
    }
}
