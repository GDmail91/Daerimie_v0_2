package org.daelimie.test.daelimie;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by YS on 2016-03-08.
 */
public class RouteAdapter extends BaseAdapter {
    private static final String TAG = "RouteAdapter";
    private ArrayList<String> m_route_type;
    private ArrayList<String> m_route_des;
    private ArrayList<String> m_route_duration;
    private LocateItemCallback callback;

    public RouteAdapter(ArrayList<String> m_route_type, ArrayList<String> m_route_des, ArrayList<String> m_route_duration, LocateItemCallback callback) {
        this.m_route_type = m_route_type;
        this.m_route_des = m_route_des;
        this.m_route_duration = m_route_duration;
        this.callback = callback;
    }


    // 현재 아이템의 수를 리턴
    @Override
    public int getCount() {
        return m_route_duration.size();
    }

    // 현재 아이템의 오브젝트를 리턴, Object를 상황에 맞게 변경하거나 리턴받은 오브젝트를 캐스팅해서 사용
    @Override
    public Object getItem(int position) {
        return m_route_duration.get(position);
    }

    // 아이템 position의 ID 값 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 출력 될 아이템 관리
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        View view = convertView;
        ViewHolder holder;

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if ( view == null ) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.route_item, parent, false);

            holder = new ViewHolder();

            holder.route_type_view = (TextView) view.findViewById(R.id.route_type);
            holder.route_des_view = (TextView) view.findViewById(R.id.route_destination);

            view.setTag(holder);
        } else {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            holder = (ViewHolder)view.getTag();

        }

        // 각 뷰에 값넣기

        // TextView에 현재 position의 문자열 추가
        holder.route_type_view.setText(Html.fromHtml(m_route_type.get(position)+" ("+m_route_duration.get(position)+")"));
        holder.route_des_view.setText(Html.fromHtml(m_route_des.get(position)));
/*
        // 버튼을 터치 했을 때 이벤트 발생
        holder.btn_test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 터치 시 해당 아이템 이름 출력
                Toast.makeText(context, "리스트 클릭 : " + m_dep.get(pos) + "\n MAC_ADDR: " + m_des.get(pos), Toast.LENGTH_SHORT).show();
            }
        });
*/
        // 리스트 아이템을 터치 했을 때 이벤트 발생
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 터치 시 해당 아이템 이름 출력, fid에 따른 뷰 띄워줌
                callback.itemChange(pos);
            }
        });

        /*
        // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
        view.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // 터치 시 해당 아이템 이름 출력
                Toast.makeText(context, "리스트 롱 클릭 : " + m_List.get(pos), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    */
        return view;
    }

    static class ViewHolder {
        TextView route_type_view;
        TextView route_des_view;
    }

    // 외부에서 아이템 추가 요청 시 사용
    public void add(String _type, String _des, String _duration) {
        m_route_type.add(_type);
        m_route_des.add(_des);
        m_route_duration.add(_duration);
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int _position) {
        m_route_type.remove(_position);
        m_route_des.remove(_position);
        m_route_duration.remove(_position);
    }


}
