package com.example.nadri4_edit1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalenderViewHolder> {

    ArrayList<Date> dateList;
    private Context mContext = null;

    //변수 선언
    public CalendarAdapter(ArrayList<Date> dateList, Context context) {
        this.dateList = dateList;
        mContext = context;
    }

    @NonNull
    @Override
    //화면을 연결하는 뷰홀더
    //달력 화면에서 보여줄 R.layout.*_item 설정
    public CalenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.calender_item, parent, false);

        return new CalenderViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    //데이터를 연결하는 뷰홀더
    public void onBindViewHolder(@NonNull CalenderViewHolder holder, int position) {

        //날짜 변수에 담기
        Date monthDate = dateList.get(position);

        //달력 초기화 ==> Date클래스에서 deprecated되어 대체된 메서드들 사용하기 위해 설정!!
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        //텍스트 색상 지정(토요일, 일요일)
        if( (position + 1) % 7 == 0){   //토요일이면
            holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorSaturday));    //colorSaturday
        }
        else if (position == 0 || position % 7 == 0) {  //일요일이면
            holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorSunday));    //colorSunday
        }//Color.parseColor("#D81B60")

        //현재 년, 월, 일
        int currentDay = CalendarUtil.selectedDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = CalendarUtil.selectedDate.get(Calendar.MONTH)+1;
        int currentYear = CalendarUtil.selectedDate.get(Calendar.YEAR);

        //넘어온 데이터
        int tailDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int tailMonth = dateCalendar.get(Calendar.MONTH)+1;
        int tailYear = dateCalendar.get(Calendar.YEAR);

        //비교해서 년,월이 같으면 진하게, 아니면 연하게 변경
        if(tailMonth == currentMonth && tailYear == currentYear){
            holder.tvDate.setTypeface(null, Typeface.BOLD);     //볼드체 설정
        }
        else {
            if( (position + 1) % 7 == 0){   //토요일이면
                holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorTailSaturday));    //colorSaturday
            }
            else if (position == 0 || position % 7 == 0) {
                holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorTailSunday));    //colorSunday
            }//Color.parseColor("#D81B60")
            else {
                holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorTail));    //회색으로 흐리게!
            }
        }

        //오늘 날짜만 초록불 들어오게!
        LocalDate toDay = LocalDate.now();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); //LocalDate포맷에 맞춰줌
        String dateNow = simpleDateFormat.format(monthDate.getTime());
        //String toDay = simpleDateFormat.format(String.valueOf(TD));   => LocalDate자체의 포맷이 yyyy-MM-dd라서 포맷해줄 필요 없음
        //String toDay = simpleDateFormat.format(CalendarUtil.selectedDate.getTimeInMillis());  //포맷은 잘 먹는데 selectedDate가 페이지 바뀌면 월도 바뀌는 애라 소용X
        if(dateNow.equals(String.valueOf(toDay))) {
            //date 똑같으면 색상 표시
            holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorToDay));
            //holder.tvDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorToDay));
            //holder.tvDate.setBackgroundResource(R.drawable.bg_round);   //배경 동그라미 생성
        }

        //날짜 변수에 담기
        int dayNum = dateCalendar.get(Calendar.DAY_OF_MONTH);

        holder.tvDate.setText(String.valueOf(dayNum));

        //제목 만들고 썸네일 적용하기
        String title, tMonth, tDay;
        if(tailMonth < 10) tMonth = "0" + tailMonth;
        else tMonth = String.valueOf(tailMonth);
        if(dayNum < 10) tDay = "0" + dayNum;
        else tDay = String.valueOf(dayNum);

        title = tailYear + "-" + tMonth + "-" + tDay;
        ReqServer.dateAlbumList.forEach(item -> {
            try {
                String tmp = item.getString("title");
                if(tmp.equals(title)){
                    Uri imageUri = Uri.parse(item.getString("thumbnail"));
                    holder.ivThumb.setClipToOutline(true);
                    Glide.with(mContext).load(imageUri).into(holder.ivThumb);
                }
            } catch (JSONException e) {
                Log.e("HWA", "달력 썸네일 적용 에러 " + e);
            }
        });

        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 이후 기능 구현
                Toast.makeText(holder.itemView.getContext(), "클릭", Toast.LENGTH_SHORT).show();

                Context context = view.getContext();
                Intent selectDay = new Intent(context, AlbumPageActivity.class);
                //Intent intent = new Intent(holder.itemView.getContext(), PageList.class);

                //넘길 데이터 : 클릭한 날짜 보내기 - day값만 보내면 됨
                int iDay = dateCalendar.get(dateCalendar.DAY_OF_MONTH);
                //Log.d("확인", String.valueOf(iDay));
                selectDay.putExtra("SelectedDATE", iDay);

                //선택한 앨범 정보를 서버 데이터에 셋팅하기
                String title, tMonth, tDay;
                if(tailMonth < 10) tMonth = "0" + tailMonth;
                else tMonth = String.valueOf(tailMonth);
                if(dayNum < 10) tDay = "0" + iDay;
                else tDay = String.valueOf(iDay);

                title = tailYear + "-" + tMonth + "-" + tDay;

                try {
                    ReqServer.album.put("title", title);
                    ReqServer.album.put("type", "dateAlbum");
                    ReqServer.dateAlbumList.forEach(item -> {
                        try {
                            if(item.getString("title").equals(title)){
                                ReqServer.album = item;
                            };
                        } catch (Exception e) {
                            Log.e("HWA", "albumList Error: " + e);
                        }
                    });
                } catch (Exception e) {
                    Log.e("HWA", "albumList Error: " + e);
                }

                //전송
                ((CalendarMainActivity)context).startActivity(selectDay);
            }
        });

    }

    @Override
    //달력 화면에 보일 item 설정
    public int getItemCount() {
        return dateList.size();
    }

    class CalenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        View parentView;
        ImageView ivThumb;

        public CalenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvCalendarDate);
            parentView = itemView.findViewById(R.id.parentView);
            ivThumb = itemView.findViewById(R.id.ivAlbumThumb);
        }
    }

}
